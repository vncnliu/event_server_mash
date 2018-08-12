package top.vncnliu.event.server.mash.base.service;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface EntityService<T,ID> {

    EntityManager getEntityManager();

    @Transactional
    default T persist(T t){
        getEntityManager().persist(t);
        return t;
    }


    default T findById(ID t) {
        return getEntityManager().find(getEntityClass(),t);
    }

    default Class<T> getEntityClass(){
        Class<?> entityClass = null;
        Type[] genericInterfaces = this.getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            Type[] genericInterfaces2 = ((Class) genericInterface).getGenericInterfaces();
            for (Type type : genericInterfaces2) {
                Type[] actualTypeArguments = ((ParameterizedType) type)
                        .getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                    entityClass = (Class<?>) actualTypeArguments[0];
                }
            }
        }

        return (Class<T>) entityClass;
    }
}
