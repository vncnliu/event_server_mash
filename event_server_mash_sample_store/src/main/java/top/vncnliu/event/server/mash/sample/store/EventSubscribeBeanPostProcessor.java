package top.vncnliu.event.server.mash.sample.store;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import top.vncnliu.event.server.mash.base.event.EventHandler;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


/**
 * User: liuyq
 * Date: 2018/7/17
 * Description: 自动扫描 {@link EventHandler} 注解的类，注册到eventbus中
 */
@Component
public class EventSubscribeBeanPostProcessor implements BeanPostProcessor {

    private final EventBus eventBus;

    @Autowired
    public EventSubscribeBeanPostProcessor(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) throws BeansException {
        if(bean!=null){
            for (Annotation annotation : bean.getClass().getAnnotations()) {
                if(annotation.annotationType().equals(EventHandler.class)){
                    eventBus.register(bean);
                    for (Method method : bean.getClass().getMethods()) {
                        for (Annotation methodAno : method.getAnnotations()) {
                            if(methodAno.annotationType().equals(Subscribe.class)){
                                for (Class<?> aClass : method.getParameterTypes()) {
                                    App.HANDLE_EVENT.add(aClass.getName());
                                }
                            }
                        }
                    }
                }
            }
        }
        return bean;
    }
}
