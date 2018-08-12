package top.vncnliu.event.server.mash.base.event;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * User: liuyq
 * Date: 7/29/18
 * Description:
 */
@Data
@Accessors(chain = true)
public class OrderCreateEvent extends BaseEvent {
    private Integer symbol_id;
    private Integer nums;
}
