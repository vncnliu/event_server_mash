package top.vncnliu.event.server.mash.base.event;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.CompletableFuture;

/**
 * User: liuyq
 * Date: 7/29/18
 * Description:
 */
@Data
@Accessors(chain = true)
public class InventoryEvent extends BaseEvent {
    private CompletableFuture completableFuture;
    private Integer symbol_id;
    private Integer nums;
}
