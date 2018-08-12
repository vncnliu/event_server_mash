package top.vncnliu.event.server.mash.base;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
/**
 * User: vncnliu
 * Date: 2018/7/26
 * Description:
 */
@Slf4j
public class ChainRespEventBus {

    private EventBus eventBus;

    public ChainRespEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public MashResp exeEvents(AbsMashEvent[] exeEvents, AbsMashEvent[] bakEvents) throws ExecutionException, InterruptedException {

        CompletableFuture<MashResp> completableFuture = new CompletableFuture<>();

        List<AbsMashEvent> backEvents = new ArrayList<>();

        for (int i = 0; i < exeEvents.length; i++) {
            AbsMashEvent now = exeEvents[i];
            AbsMashEvent bak = bakEvents[i];
            AbsMashEvent next;
            if(i==exeEvents.length-1){
                next=null;
            }else {
                next=exeEvents[i+1];
            }
            now.getRespFuture().thenAccept(mashResp -> {
                log.debug("receive complete:{}",mashResp);
                if(mashResp.getCode()!=Constant.ErrorCode.SUCCESS){
                    if(backEvents.isEmpty()){
                        completableFuture.complete(mashResp);
                    }else {
                        exeBakEvents(backEvents,completableFuture);
                    }
                }else {
                    if(next!=null){
                        eventBus.post(next.setContext(mashResp));
                        if(bak!=null){
                            backEvents.add(bak);
                        }
                    }else {
                        try {
                            completableFuture.complete(now.getRespFuture().get());
                        } catch (Exception e) {
                            log.error("执行回滚事件异常："+e.getMessage(),e);
                            completableFuture.complete(new MashResp(Constant.ErrorCode.MASH_BAK_ERROR,0,e.getMessage()));
                        }
                    }
                }
            });
        }

        eventBus.post(exeEvents[0]);

        return completableFuture.get();
    }

    private void exeBakEvents(List<AbsMashEvent> bakEvents, CompletableFuture<MashResp> completableFuture) {

        for (int i = bakEvents.size()-1; i >= 0; i--) {
            AbsMashEvent now = bakEvents.get(i);
            AbsMashEvent next;
            if(i==0){
                next=null;
            }else {
                next=bakEvents.get(i-1);
            }
            now.getRespFuture().thenAccept(mashResp -> {
                if(next!=null){
                    eventBus.post(next);
                }else {
                    completableFuture.complete(mashResp);
                }
            });
        }

        eventBus.post(bakEvents.get(bakEvents.size()-1));
    }
}
