package top.vncnliu.event.server.mash.base;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.transaction.annotation.Transactional;
import top.vncnliu.event.server.mash.base.entity.EventTask;
import top.vncnliu.event.server.mash.base.event.BaseEvent;
import top.vncnliu.event.server.mash.base.service.IEventTaskService;
import top.vncnliu.event.server.mash.base.service.IRedisService;
import top.vncnliu.event.server.mash.base.util.Cache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * User: vncnliu
 * Date: 2018/7/31
 * Description:
 */
@Slf4j
public class EventProducer {

    private IEventTaskService eventTaskService;
    private IRedisService redisService;
    private DatagramChannel channel;
    private String hostName;
    private Integer port;
    private ExecutorService executorService = Executors.newFixedThreadPool(100);

    private Cache<Integer, CompletableFuture<String>> resultFutures = new Cache<>(5 * 1000, 5 * 1000, entry -> {
        EventTask sourceTask = eventTaskService.findById(entry.getKey());
        if (sourceTask.getStatus() == Constant.TASK_STATUS.EXECUTED.getCode()) {
            entry.getValue().complete(sourceTask.getResult());
        } else {
            entry.getValue().complete("time out");
        }
    });

    public EventProducer(IEventTaskService eventTaskService, IRedisService redisService, String hostName) throws IOException {
        this.eventTaskService = eventTaskService;
        this.redisService = redisService;
        this.hostName = hostName;
        channel = DatagramChannel.open();
        Selector selector = Selector.open();
        port = Utils.init(channel, selector, hostName);
        log.debug("register eventProducer ip:{} port:{}", hostName, port);
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    selector.select();
                    Iterator ite = selector.selectedKeys().iterator();
                    while (ite.hasNext()) {
                        SelectionKey key = (SelectionKey) ite.next();
                        ite.remove();
                        if (key.isReadable()) {
                            ByteBuffer buf = ByteBuffer.allocate(65535);
                            channel.receive(buf);
                            byte[] data = buf.array();
                            String msg = new String(data).trim();
                            Integer id = Integer.parseInt(msg);
                            log.debug("eventProducer-{}-{} complete event {} on {}", hostName, port, id, System.currentTimeMillis());
                            EventTask eventTask = eventTaskService.findById(id);
                            CompletableFuture<String> future = resultFutures.get(id);
                            if(future!=null){
                                resultFutures.get(id).complete(eventTask.getResult());
                                resultFutures.removeByKey(id);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }).start();
    }

    public EventTask createEvent(BaseEvent event) {
        EventTask eventTask = saveEventByBaseEvent(event);
        event.setReply(Constant.REPLY_STATUS.FALSE.getStatus());
        sendTaskEvent(eventTask);
        return eventTask;
    }

    public CompletableFuture<String> createEventWaitForResult(BaseEvent event) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        event.setReply(Constant.REPLY_STATUS.TRUE.getStatus());
        EventTask eventTask = saveEventByBaseEvent(event);
        resultFutures.put(eventTask.getId(), completableFuture);
        sendTaskEvent(eventTask);
        log.debug("eventProducer-{}-{} create event {} on {}", hostName, port, eventTask.getId(), System.currentTimeMillis());
        return completableFuture;
    }

    private EventTask saveEventByBaseEvent(BaseEvent event) {
        String partitionKey = event.getClass().getName() + "-region";
        EventTask eventTask = new EventTask()
                .setName(event.getClass().getName())
                .setRegion((int) (redisService.incr(partitionKey, 1, TimeUnit.DAYS) % 10))
                .setFront_event(event.getFront_event_task_id())
                .setSource_event(event.getSource_event_task_id())
                .setData(JSON.toJSONString(event))
                .setStatus(Constant.TASK_STATUS.UN_SUBMIT.getCode())
                .setTail(event.getTail())
                .setReply(event.getReply())
                .setCreate_ip(hostName)
                .setCreate_port(port);
        if (eventTask.getRegion() >= Integer.MAX_VALUE) {
            redisService.set(partitionKey, 0 + "", 1, TimeUnit.DAYS);
        }
        eventTask = eventTaskService.persist(eventTask);
        return eventTask;
    }

    private void sendTaskEvent(EventTask eventTask) {
        executorService.submit(() -> {
            String nodeInfo = redisService.get(eventTask.getName() + "-node-" + eventTask.getRegion());
            if (Strings.isNotBlank(nodeInfo)) {
                String[] address = nodeInfo.split(":");
                try {
                    sendMsg(eventTask.getId() + "", new InetSocketAddress(address[0], Integer.parseInt(address[1])));
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }

    @Transactional
    public int commitStatus(BaseEvent baseEvent, int code) {
        int result = eventTaskService.updateStatus(baseEvent.getEvent_task_id(), code);
        executorService.submit(() -> {
            if (code == Constant.TASK_STATUS.EXECUTED.getCode()) {
                if (baseEvent.getTail() == 1) {
                    try {
                        EventTask sourceTask = eventTaskService.findById(baseEvent.getSource_event_task_id());
                        sourceTask.setResult(baseEvent.getSource_result());
                        eventTaskService.save(sourceTask);
                        if(sourceTask.getReply()==Constant.REPLY_STATUS.TRUE.getStatus()) {
                            CompletableFuture<String> future = resultFutures.get(baseEvent.getSource_event_task_id());
                            if (future != null) {
                                future.complete(sourceTask.getResult());
                            } else {
                                sendMsg(sourceTask.getId() + "", new InetSocketAddress(sourceTask.getCreate_ip(), sourceTask.getCreate_port()));
                            }
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    redisService.releaseLock(baseEvent.getEvent_task_id() + "", null);
                }
            }
        });
        log.debug("eventProducer-{}-{} source event {} in event {} change status {} on {}",
                hostName, port, baseEvent.getSource_event_task_id(), baseEvent.getEvent_task_id(),
                code,
                System.currentTimeMillis());
        return result;
    }

    private void sendMsg(String msg, InetSocketAddress inetSocketAddress) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(msg.getBytes().length);
        buf.put(msg.getBytes());
        buf.flip();
        channel.send(buf, inetSocketAddress);
    }
}
