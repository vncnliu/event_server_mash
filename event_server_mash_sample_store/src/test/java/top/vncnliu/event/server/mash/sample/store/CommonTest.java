package top.vncnliu.event.server.mash.sample.store;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import top.vncnliu.event.server.mash.base.event.OrderCreateEvent;

/**
 * User: vncnliu
 * Date: 2018/7/30
 * Description:
 */
public class CommonTest {

    @Test
    public void testJson(){
        System.out.println(JSON.toJSONString(new OrderCreateEvent().setNums(1).setSymbol_id(1)));
    }
}
