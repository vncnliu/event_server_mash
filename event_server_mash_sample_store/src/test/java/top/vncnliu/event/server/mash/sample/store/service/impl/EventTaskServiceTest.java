package top.vncnliu.event.server.mash.sample.store.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import top.vncnliu.event.server.mash.base.entity.EventTask;
import top.vncnliu.event.server.mash.base.service.IEventTaskService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EventTaskServiceTest {

    @Autowired
    private IEventTaskService eventTaskService;

    @Test
    @Transactional
    void saveTest() {
        eventTaskService.save(new EventTask());
    }
}