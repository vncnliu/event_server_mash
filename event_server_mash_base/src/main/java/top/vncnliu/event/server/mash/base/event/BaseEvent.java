package top.vncnliu.event.server.mash.base.event;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * User: vncnliu
 * Date: 2018/7/30
 * Description:
 */
@Data
@Accessors(chain = true)
public class BaseEvent {
    private Integer event_task_id;
    private Integer front_event_task_id;
    private Integer source_event_task_id;
    private Short tail;
    private Short reply;
    private String source_result;
}
