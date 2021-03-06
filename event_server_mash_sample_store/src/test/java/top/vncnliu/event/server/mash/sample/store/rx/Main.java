package top.vncnliu.event.server.mash.sample.store.rx;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.lang.reflect.Proxy;
import java.util.function.Consumer;

/**
 * User: vncnliu
 * Date: 2018/8/30
 * Description:
 */
public class Main {

    @Test
    public void main() throws InterruptedException {
        String greeting = "Hello world!";

        Observable<String> observable = Observable.just(greeting);

        observable.subscribe(System.out::println);
        observable.subscribe(System.out::println);

        PublishSubject<String> test = PublishSubject.create();
        PublishSubject<String> test2 = PublishSubject.create();
        test2.observeOn(Schedulers.newThread()).subscribe(s -> System.out.println(s+"1"));
        test.observeOn(Schedulers.newThread()).subscribe(s -> {
            Thread.sleep(1000);
            System.out.println(s+"2");
        });
        test.onNext("a");
        test.onNext("a");
        test2.onNext("a");
        test2.onNext("a");
        Thread.sleep(10000);
    }

    @Test
    public void testReac(){
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error " + error),
                () -> {System.out.println("Done");});
    }
}
