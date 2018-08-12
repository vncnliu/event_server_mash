package top.vncnliu.event.server.mash.sample.store.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.Test;

import java.util.concurrent.Executors;

/**
 * User: vncnliu
 * Date: 2018/8/6
 * Description:
 */
public class TestEventBus {

    @Test
    public void main() throws InterruptedException {
        EventBus eventBus = new AsyncEventBus(Executors.newFixedThreadPool(100));
        eventBus.register(new TestEvent1Handle());
        eventBus.register(new TestEvent2Handle());
        for (int i = 0; i < 10; i++) {
            eventBus.post(new TestEvent1());
            eventBus.post(new TestEvent1());
            eventBus.post(new TestEvent2());
        }
        Thread.sleep(10000);
    }

    class TestEvent1 {

    }

    class TestEvent2 {

    }

    class TestEvent1Handle {
        @Subscribe
        public void test(TestEvent1 event1) throws InterruptedException {
            System.out.println("run test event1");
            Thread.sleep(1000);
        }
    }

    class TestEvent2Handle {
        @Subscribe
        public void test(TestEvent2 event2) throws InterruptedException {
            System.out.println("run test event2");
            Thread.sleep(1000);
        }
    }
}
