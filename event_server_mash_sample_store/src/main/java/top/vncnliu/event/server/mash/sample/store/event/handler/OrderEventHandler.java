package top.vncnliu.event.server.mash.sample.store.event.handler;

import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.vncnliu.event.server.mash.base.Constant;
import top.vncnliu.event.server.mash.base.EventProducer;
import top.vncnliu.event.server.mash.base.event.BaseEvent;
import top.vncnliu.event.server.mash.base.event.EventHandler;
import top.vncnliu.event.server.mash.base.event.InventoryEvent;
import top.vncnliu.event.server.mash.base.event.OrderCreateEvent;

import java.io.IOException;

/**
 * User: liuyq
 * Date: 7/29/18
 * Description:
 */
@Slf4j
@Component
@EventHandler
public class OrderEventHandler {

    private EventProducer eventProducer;

    @Autowired
    public OrderEventHandler(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    @Subscribe
    public void createOrder(OrderCreateEvent event) throws IOException {
        //生成库存事件
        BaseEvent inventoryEvent = new InventoryEvent().setNums(1)
                .setSymbol_id(1)
                .setSource_event_task_id(event.getSource_event_task_id())
                .setFront_event_task_id(event.getEvent_task_id())
                .setTail((short) 1);
        if(event.getSource_event_task_id()==null||event.getSource_event_task_id()==0){
            inventoryEvent.setSource_event_task_id(event.getEvent_task_id());
        }
        eventProducer.createEvent(inventoryEvent);
        //标记事件状态
        int result = eventProducer.commitStatus(event,Constant.TASK_STATUS.EXECUTED.getCode());
        if(result!=1){
            log.error("状态更新异常");
        }
    }

    @Subscribe
    public void inventory(InventoryEvent event) throws IOException {
        //标记事件状态
        int result = eventProducer.commitStatus(event,Constant.TASK_STATUS.EXECUTED.getCode());
        if(result!=1){
            log.error("状态更新异常");
        }
    }
}
