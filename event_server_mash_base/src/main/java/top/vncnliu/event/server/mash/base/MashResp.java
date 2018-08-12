package top.vncnliu.event.server.mash.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * User: liuyq
 * Date: 2018/7/25
 * Description:
 */
@Data
@AllArgsConstructor
@Accessors(chain = true)
public class MashResp {
    private Constant.ErrorCode code;
    private Integer eventId;
    private Object data;
}
