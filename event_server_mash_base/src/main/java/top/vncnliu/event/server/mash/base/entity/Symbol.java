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
@Table(name = "t_symbol", schema = "event_mash_inventory", catalog = "event_mash_inventory")
@DynamicInsert
@Accessors(chain = true)
public class Symbol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
}
