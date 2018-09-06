package top.vncnliu.event.server.mash.sample.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User: liuyq
 * Date: 2018/7/26
 * Description:
 */
@SpringBootApplication
@EnableScheduling
@EntityScan("top.vncnliu.event.server.mash")
@ComponentScan("top.vncnliu.event.server.mash")
public class App {

    public static final String SERVER_ID = "event-mash-"+UUID.randomUUID().toString();
    public static final List<String> HANDLE_EVENT = new ArrayList<>();

    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }

}
