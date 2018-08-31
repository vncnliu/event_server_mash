package top.vncnliu.event.server.mash.base;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import top.vncnliu.event.server.mash.base.entity.EventTask;
import top.vncnliu.event.server.mash.base.event.BaseEvent;
import top.vncnliu.event.server.mash.base.service.IEventTaskService;
import top.vncnliu.event.server.mash.base.service.IRedisService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Map;
import java.util.concurrent.*;

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
    private Map<Integer,CompletableFuture<String>> resultFutures = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(100);

    public EventProducer(IEventTaskService eventTaskService, IRedisService redisService, String hostName) throws IOException {
        this.eventTaskService = eventTaskService;
        this.redisService = redisService;
        channel = DatagramChannel.open();
        this.hostName = hostName;
        port = Utils.init(channel,hostName);
        log.debug("register eventProducer ip:{} port:{}",hostName,port);
        new Thread(() -> {
            try {
                ByteBuffer buf = ByteBuffer.allocate(65535);
                while (true){
                    channel.receive(buf);
                    byte[] data = buf.array();
                    String msg = new String(data).trim();
                    Integer id = Integer.parseInt(msg);
                    EventTask eventTask = eventTaskService.findById(id);
                    resultFutures.get(id).complete(eventTask.getResult());
                    resultFutures.remove(id);
                    log.debug("eventProducer-{}-{} complete event {} on {}",hostName,port,id,System.currentTimeMillis());
                }
            } catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }).start();
    }

    public EventTask createEvent(BaseEvent event) {
        EventTask eventTask = saveEventByBaseEvent(event);
        sendTaskEvent(eventTask);
        return eventTask;
    }

    public CompletableFuture<String> createEventWaitForResult(BaseEvent event) {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        EventTask eventTask = saveEventByBaseEvent(event);
        resultFutures.put(eventTask.getId(),completableFuture);
        sendTaskEvent(eventTask);
        log.debug("eventProducer-{}-{} create event {} on {}",hostName,port,eventTask.getId(),System.currentTimeMillis());
        return completableFuture;
    }

    private EventTask saveEventByBaseEvent(BaseEvent event) {
        String partitionKey = event.getClass().getName()+"-region";
        EventTask eventTask = new EventTask()
                .setName(event.getClass().getName())
                .setRegion((int) (redisService.incr(partitionKey,1,TimeUnit.DAYS)%10))
                .setFront_event(event.getFront_event_task_id())
                .setSource_event(event.getSource_event_task_id())
                .setData(JSON.toJSONString(event))
                .setStatus(Constant.TASK_STATUS.UN_SUBMIT.getCode())
                .setTail(event.getTail())
                .setCreate_ip(hostName)
                .setCreate_port(port);
        if(eventTask.getRegion()>=Integer.MAX_VALUE){
            redisService.set(partitionKey,0+"",1,TimeUnit.DAYS);
        }
        eventTask = eventTaskService.persist(eventTask);
        return eventTask;
    }

    private void sendTaskEvent(EventTask eventTask) {
        executorService.submit(() -> {
            String nodeInfo = redisService.get(eventTask.getName()+"-node-"+eventTask.getRegion());
            if(Strings.isNotBlank(nodeInfo)){
                String[] address = nodeInfo.split(":");
                try {
                    sendMsg(eventTask.getId()+"",new InetSocketAddress(address[0],Integer.parseInt(address[1])));
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
        });
    }

    public int commitStatus(BaseEvent baseEvent, int code) {
        int result = eventTaskService.updateStatus(baseEvent.getEvent_task_id(),code);
        executorService.submit(() -> {
            if(code==Constant.TASK_STATUS.EXECUTED.getCode()){
                if(baseEvent.getTail()==1){
                    try {
                        CompletableFuture<String> future = resultFutures.get(baseEvent.getSource_event_task_id());
                        EventTask sourceTask = eventTaskService.findById(baseEvent.getSource_event_task_id());
                        if(future!=null){
                            future.complete(sourceTask.getResult());
                        } else {
                            sendMsg(sourceTask.getId()+"",new InetSocketAddress(sourceTask.getCreate_ip(),sourceTask.getCreate_port()));
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage(),e);
                    }
                    redisService.releaseLock(baseEvent.getEvent_task_id()+"",null);
                }
            }
        });
        log.debug("eventProducer-{}-{} source event {} in event {} change status {} on {}",
                hostName,port,baseEvent.getSource_event_task_id(),baseEvent.getEvent_task_id(),
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
