package top.vncnliu.event.server.mash.base;

import java.util.concurrent.CompletableFuture;

/**
 * User: liuyq
 * Date: 2018/7/25
 * Description:
 */
public abstract class AbsMashEvent {
    private MashResp context;
    private CompletableFuture<MashResp> respFuture = new CompletableFuture<>();

    public MashResp getContext() {
        return context;
    }

    public AbsMashEvent setContext(MashResp context) {
        this.context = context;
        return this;
    }

    public CompletableFuture<MashResp> getRespFuture() {
        return respFuture;
    }
}
