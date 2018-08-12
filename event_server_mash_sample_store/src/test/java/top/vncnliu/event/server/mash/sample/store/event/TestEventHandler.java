package top.vncnliu.event.server.mash.sample.store.event;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;
import top.vncnliu.event.server.mash.base.BackEvent;
import top.vncnliu.event.server.mash.base.Constant;
import top.vncnliu.event.server.mash.base.MashResp;

/**
 * User: liuyq
 * Date: 2018/7/25
 * Description:
 */
class TestEventHandler {
    @Subscribe
    public void handleCash(CashEvent baseEvent) {
        System.out.println("CashEvent handler ");
        //System.out.println(baseEvent.getContext());
        baseEvent.getRespFuture().complete(new MashResp(Constant.ErrorCode.ORDER_ERROR,0,"CashEvent"));
    }
    @Subscribe
    public void handleOrder(OrderEvent baseEvent) {
        System.out.println("OrderEvent handler ");
        baseEvent.getRespFuture().complete(new MashResp(Constant.ErrorCode.SUCCESS,0,"OrderEvent"));
    }
    @Subscribe
    public void handleInventory(InventoryEvent baseEvent) {
        System.out.println("InventoryEvent handler ");
        baseEvent.getRespFuture().complete(new MashResp(Constant.ErrorCode.SUCCESS,0,"InventoryEvent"));
    }
    @Subscribe
    public void baseHandle(BaseEvent baseEvent) {
        System.out.println("handler ");
        baseEvent.getRespFuture().complete(new MashResp(Constant.ErrorCode.SUCCESS,0,"base"));
    }
    @Subscribe
    public void baseHandle(BackEvent baseEvent) {
        System.out.println("BackEvent handler ");
        baseEvent.getRespFuture().complete(new MashResp(Constant.ErrorCode.SUCCESS,0,"BackEvent "+baseEvent.getId()));
    }
    @Subscribe
    public void baseHandle(DeadEvent baseEvent) {
        System.out.println("DeadEvent handler ");
    }
}
