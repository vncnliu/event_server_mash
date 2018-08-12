package top.vncnliu.event.server.mash.base;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * User: vncnliu
 * Date: 2018/7/26
 * Description:
 */
@Data
@AllArgsConstructor
public class BackEvent extends AbsMashEvent {
    private int id;
}
