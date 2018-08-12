package top.vncnliu.event.server.mash.base.service;

import top.vncnliu.event.server.mash.base.entity.EventTask;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * User: vncnliu
 * Date: 2018/7/27
 * Description:
 */
public interface IEventTaskService extends EntityService<EventTask,Integer> {
    List<EventTask> findPendingEventTask(int code, Collection<String> events);

    int updateStatus(int event_task_id, int code);

    void save(EventTask eventTask);

    List<EventTask> findEventTaskByTypeAndPartition(String event, Set<Integer> integers);

    List<EventTask> findEventByFrontId(int event_task_id);
}
