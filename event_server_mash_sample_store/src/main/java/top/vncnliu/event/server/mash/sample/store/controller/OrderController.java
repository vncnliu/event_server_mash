package top.vncnliu.event.server.mash.sample.store.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import top.vncnliu.event.server.mash.base.EventProducer;
import top.vncnliu.event.server.mash.base.entity.EventTask;
import top.vncnliu.event.server.mash.base.event.OrderCreateEvent;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * User: liuyq
 * Date: 7/29/18
 * Description:
 */
@Slf4j
@RestController
public class OrderController {

    private EventProducer eventProducer;

    @Autowired
    public OrderController(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    @PostMapping("/create/async")
    public DeferredResult<String> createAsync(){
        DeferredResult<String> deferredResult = new DeferredResult<>();
        CompletableFuture<String> completableFuture = eventProducer.createEventWaitForResult(new OrderCreateEvent().setNums(1).setSymbol_id(1));
        completableFuture.whenComplete((s, throwable) -> {
            deferredResult.setResult(s);
        });
        return deferredResult;
    }

    @PostMapping("create")
    public String create() {
        EventTask eventTask = eventProducer.createEvent(new OrderCreateEvent().setNums(1).setSymbol_id(1));
        return JSON.toJSONString(eventTask);
    }

    @GetMapping("test")
    public String test(@RequestParam(name = "imei") String imeis) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        Random random = new Random();
        String[] imei = imeis.split(",");
        for (String s : imei) {
            if(random.nextInt(100)>20){
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("msisdn_md5",UUID.randomUUID().toString());
                jsonObject1.put("imei",s);
                jsonArray.add(jsonObject1);
            }
        }
        jsonObject.put("phone",jsonArray);
        jsonObject.put("status",0);
        return jsonObject.toJSONString();
    }
}
