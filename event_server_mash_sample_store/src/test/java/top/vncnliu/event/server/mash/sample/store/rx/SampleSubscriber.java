package top.vncnliu.event.server.mash.sample.store.rx;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

/**
 * User: vncnliu
 * Date: 2018/9/6
 * Description:
 */
public class SampleSubscriber<T> extends BaseSubscriber<T> {

    public void hookOnSubscribe(Subscription subscription) {
        System.out.println("Subscribed");
        request(1);
    }

    public void hookOnNext(T value) {
        System.out.println(value);
        request(1);
    }

    @Override
    protected void hookOnComplete() {
        super.hookOnComplete();
    }
}
