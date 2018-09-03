package top.vncnliu.event.server.mash.base.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Date;

/**
 * User: vncnliu
 * Date: 2018/7/27
 * Description:
 */
@Entity
@Data
@Table(name = "event_task", schema = "event_server_mash", catalog = "event_server_mash")
@DynamicInsert
@Accessors(chain = true)
public class EventTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 分区
     */
    private Integer region;
    /**
     * 事件名称
     */
    private String name;
    /**
     * 数据
     */
    private String data;
    /**
     * 生产者ip
     */
    private String create_ip;
    /**
     * 生产者port
     */
    private int create_port;
    /**
     * 源事件ID
     */
    private Integer source_event;
    /**
     * 前置事件ID
     */
    private Integer front_event;
    /**
     * 是否尾任务
     * @see top.vncnliu.event.server.mash.base.Constant.TAIL_STATUS
     */
    private Short tail;
    /**
     * 是否需要回复
     * @see top.vncnliu.event.server.mash.base.Constant.REPLY_STATUS
     */
    private Short reply;
    /**
     * 事件执行结果
     */
    private String result;
    @Column(updatable = false)
    private Date time_enable;
    @Column(updatable = false)
    private Date time_create;
    @Column(updatable = false)
    private Date time_modified;
    /**
     * 事件状态
     * @see top.vncnliu.event.server.mash.base.Constant.TASK_STATUS
     */
    private Integer status;
}
