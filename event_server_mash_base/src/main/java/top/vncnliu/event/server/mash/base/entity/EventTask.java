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
    private Integer region;
    private String name;
    private String data;
    private String create_ip;
    private int create_port;
    /**
     * 源事件ID
     */
    private Integer source_event;
    /**
     * 前置事件ID
     */
    private Integer front_event;
    private Short tail;
    private String result;
    @Column(updatable = false)
    private Date time_enable;
    @Column(updatable = false)
    private Date time_create;
    @Column(updatable = false)
    private Date time_modified;
    /**
     * 3 待通知
     */
    private Integer status;
}
