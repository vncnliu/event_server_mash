package top.vncnliu.event.server.mash.sample.store.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.vncnliu.event.server.mash.base.event.BaseEvent;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * User: vncnliu
 * Date: 2018/9/6
 * Description:
 */
@Aspect
@Component
public class CacheSqlAspect {

    public final static ConcurrentHashMap<String,SqlCache> sqlCaches = new ConcurrentHashMap<>();

    @Autowired
    private DataSource dataSource;

    class SqlCache {
        private Connection connection;
        private String sql;

        public Connection getConnection() {
            return connection;
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }
    }

    // pointCut
    @Pointcut("@annotation(CacheSql)")
    public void log() {
        System.out.println("test aspect");
    }

    @Before("log()")
    public void test(JoinPoint joinPoint){
        try {
            BaseEvent baseEvent = (BaseEvent) joinPoint.getArgs()[0];
            if(sqlCaches.get(baseEvent.getEvent_task_id().toString())==null){
                SqlCache sqlCache = new SqlCache();
                sqlCache.setConnection(dataSource.getConnection());
            }
            System.out.println("test aspect");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
