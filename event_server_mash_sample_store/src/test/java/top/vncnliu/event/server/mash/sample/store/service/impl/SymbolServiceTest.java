package top.vncnliu.event.server.mash.sample.store.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import top.vncnliu.event.server.mash.base.event.BaseEvent;
import top.vncnliu.event.server.mash.sample.store.service.ISymbolService;

import java.util.concurrent.CountDownLatch;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SymbolServiceTest {

    @Autowired
    private ISymbolService symbolService;

    @Test
    void main() throws InterruptedException {
        //symbolService.persist(new Symbol().setName("test"));
        //symbolService.insert();
        int size=1;
        CountDownLatch main = new CountDownLatch(1);
        CountDownLatch child = new CountDownLatch(size);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    main.await();
                    symbolService.insertSpring();
                    //symbolService.insertSpring2();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    child.countDown();
                }
            }).start();
        }
        main.countDown();
        child.await();
    }

    @Test
    void testOut(){
        symbolService.testOut(new BaseEvent());
    }

}