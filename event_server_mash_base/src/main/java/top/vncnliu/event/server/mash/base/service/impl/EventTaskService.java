package top.vncnliu.event.server.mash.base.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import top.vncnliu.event.server.mash.base.Constant;
import top.vncnliu.event.server.mash.base.entity.EventTask;
import top.vncnliu.event.server.mash.base.service.IEventTaskService;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * User: vncnliu
 * Date: 2018/7/27
 * Description:
 */
@Service
public class EventTaskService implements IEventTaskService {

    private final EntityManager entityManager;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public EventTaskService(EntityManager entityManager, NamedParameterJdbcTemplate jdbcTemplate) {
        this.entityManager = entityManager;
        this.namedParameterJdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<EventTask> findPendingEventTask(int code, Collection<String> events) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("events", events);
        parameters.addValue("status", code);
        return namedParameterJdbcTemplate.query(
                "select * from event_task where status=:status and name in (:events)",parameters,
                new BeanPropertyRowMapper<>(EventTask.class));
    }

    @Override
    public int updateStatus(int event_task_id, int code) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", event_task_id).addValue("status", code);
        return namedParameterJdbcTemplate.update("update event_task set status=:status where id=:id",parameters);
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void save(EventTask eventTask){
        entityManager.persist(eventTask);
    }

    @Override
    public List<EventTask> findEventTaskByTypeAndPartition(String event, Set<Integer> integers) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("event", event);
        parameters.addValue("status", Constant.TASK_STATUS.UN_SUBMIT.getCode());
        parameters.addValue("regions", integers);
        return namedParameterJdbcTemplate.query(
                "select * from event_task where status=:status and name=:event and region in (:regions)",parameters,
                new BeanPropertyRowMapper<>(EventTask.class));
    }

    @Override
    public List<EventTask> findEventByFrontId(int event_task_id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("front_event", event_task_id);
        return namedParameterJdbcTemplate.query(
                "select * from event_task where front_event=:front_event",parameters,
                new BeanPropertyRowMapper<>(EventTask.class));
    }
}
