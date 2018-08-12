package top.vncnliu.event.server.mash.sample.store;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.vncnliu.event.server.mash.sample.store.event.handler.OrderEventHandler;

/**
 * User: vncnliu
 * Date: 2018/7/27
 * Description:
 */
@Configuration
public class EventBusConfig {

    @Bean
    public EventBus registerEventBus(){
        EventBus eventBus = new EventBus();
        return eventBus;
    }
}
