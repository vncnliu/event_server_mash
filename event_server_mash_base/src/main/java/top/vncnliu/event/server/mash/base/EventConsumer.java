package top.vncnliu.event.server.mash.base;

import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.EventBus;
import io.netty.util.internal.ConcurrentSet;
import lombok.extern.slf4j.Slf4j;
import top.vncnliu.event.server.mash.base.entity.EventTask;
import top.vncnliu.event.server.mash.base.event.BaseEvent;
import top.vncnliu.event.server.mash.base.service.IEventTaskService;
import top.vncnliu.event.server.mash.base.service.IRedisService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * User: vncnliu
 * Date: 2018/7/31
 * Description:
 */
@Slf4j
public class EventConsumer {

    private DatagramChannel channel;

    private IEventTaskService eventTaskService;
    private IRedisService redisService;
    private EventBus eventBus;
    private String hostName;
    private int port;

    private Map<String,Set<Integer>> eventPartitions = new ConcurrentHashMap<>();
    private Set<String> events = new ConcurrentSet<>();

    private static final ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();

    public EventConsumer(IRedisService redisService, String hostName, EventBus eventBus, IEventTaskService eventTaskService) throws IOException {
        this.eventTaskService = eventTaskService;
        this.redisService = redisService;
        this.eventBus = eventBus;
        this.hostName = hostName;
        channel = DatagramChannel.open();
        port = Utils.init(channel,hostName);
        log.debug("register eventConsumer ip:{} port:{}",hostName,port);
        es.scheduleAtFixedRate(this::setNodeInfo,0,10,TimeUnit.SECONDS);
        //延迟去数据
        es.scheduleAtFixedRate(this::pullEvent,0,10,TimeUnit.SECONDS);
        new Thread(() -> {
            try {
                ByteBuffer buf = ByteBuffer.allocate(65535);
                while (true){
                    try {
                        channel.receive(buf);
                        byte[] data = buf.array();
                        String msg = new String(data).trim();
                        Integer id = Integer.parseInt(msg);
                        log.debug("eventConsumer-{}-{} receive event {} on {}",hostName,port,id,System.currentTimeMillis());
                        EventTask eventTask = eventTaskService.findById(id);
                        BaseEvent baseEvent = JSON.parseObject(eventTask.getData(), (Type) Class.forName(eventTask.getName()));
                        baseEvent.setEvent_task_id(eventTask.getId())
                                .setFront_event_task_id(eventTask.getFront_event())
                                .setSource_event_task_id(baseEvent.getSource_event_task_id())
                                .setTail(eventTask.getTail());
                        postEvent(baseEvent);
                    } catch (Exception e) {
                        log.error(e.getMessage(),e);
                    } finally {
                        buf.clear();
                    }
                }
            } catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }).start();
    }

    public void registerEvent(Collection<String> events){
        this.events.addAll(events);
        setNodeInfo();
    }

    private void postEvent(BaseEvent baseEvent){
        if(redisService.lock(baseEvent.getEvent_task_id()+"",null,0)){
            eventBus.post(baseEvent);
        }
    }

    private void pullEvent() {
        for (String event : events) {
            List<EventTask> eventTasks = eventTaskService.findEventTaskByTypeAndPartition(event,eventPartitions.get(event));
            for (EventTask eventTask : eventTasks) {
                try {
                    BaseEvent baseEvent = JSON.parseObject(eventTask.getData(), (Type) Class.forName(eventTask.getName()));
                    baseEvent.setEvent_task_id(eventTask.getId());
                    postEvent(baseEvent);
                } catch (ClassNotFoundException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
    }

    private void setNodeInfo() {
        try {
            for (String handleEvent : events) {
                String redisKey = handleEvent+"-nodes";
                String nodeAddr = channel.socket().getLocalAddress().getHostAddress()+":"+channel.socket().getLocalPort();
                Map<String,String> oldNodes = redisService.hgetAll(redisKey);
                Map<String,String> nodes = new HashMap<>();
                if(oldNodes!=null){
                    Set<String> keys = oldNodes.keySet();
                    for (String s : keys) {
                        long time = Long.parseLong(oldNodes.get(s));
                        if(System.currentTimeMillis()-time<2000){
                            nodes.put(s,oldNodes.get(s));
                        }
                    }
                }
                nodes.put(nodeAddr,System.currentTimeMillis()+"");
                redisService.delete(redisKey);
                redisService.hsetAll(redisKey,nodes);

                List<String> nodeList = new ArrayList<>(nodes.keySet());
                Map<Integer,Set<Integer>> nodeKeys = new HashMap<>();

                for (int i = 0; i < nodeList.size(); i++) {
                    nodeKeys.put(i,new HashSet<>());
                }

                for (int i = 0; i < 10; i++) {
                    nodeKeys.get(i%nodeKeys.size()).add(i);
                    redisService.set(handleEvent+"-node-"+i,nodeList.get(i%nodeKeys.size()),30,TimeUnit.SECONDS);
                }

                for (int i = 0; i < nodeList.size(); i++) {
                    if(nodeAddr.equals(nodeList.get(i))){
                        eventPartitions.put(handleEvent,nodeKeys.get(i));
                    }
                    redisService.setSet(nodeList.get(i),
                            nodeKeys.get(i).stream().map(tmp -> tmp+"").collect(Collectors.toSet()),1,TimeUnit.HOURS);
                }
            }
        } catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

}
