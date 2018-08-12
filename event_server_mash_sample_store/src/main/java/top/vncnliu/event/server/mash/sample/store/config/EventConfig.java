package top.vncnliu.event.server.mash.sample.store.config;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.vncnliu.event.server.mash.base.EventConsumer;
import top.vncnliu.event.server.mash.base.EventProducer;
import top.vncnliu.event.server.mash.base.service.IEventTaskService;
import top.vncnliu.event.server.mash.base.service.IRedisService;

import java.io.IOException;

import static top.vncnliu.event.server.mash.sample.store.App.HANDLE_EVENT;

/**
 * User: vncnliu
 * Date: 2018/7/31
 * Description:
 */
@Slf4j
@Configuration
public class EventConfig {

    private IEventTaskService eventTaskService;
    private IRedisService redisService;
    private EventBus eventBus;

    @Autowired
    public EventConfig(IEventTaskService eventTaskService, IRedisService redisService, EventBus eventBus) {
        this.eventTaskService = eventTaskService;
        this.redisService = redisService;
        this.eventBus = eventBus;
    }

    @Bean
    public EventProducer buildEventProducer() throws IOException {
        return new EventProducer(eventTaskService, redisService, "192.168.1.155");
    }

    @Bean
    public EventConsumer buildEventConsumer() throws IOException {
        EventConsumer eventConsumer = new EventConsumer(redisService,"192.168.1.155",eventBus, eventTaskService);
        eventConsumer.registerEvent(HANDLE_EVENT);
        return eventConsumer;
    }
}
