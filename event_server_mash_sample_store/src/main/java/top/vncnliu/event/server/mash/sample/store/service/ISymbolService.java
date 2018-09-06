package top.vncnliu.event.server.mash.sample.store.service;

import org.springframework.transaction.annotation.Transactional;
import top.vncnliu.event.server.mash.base.entity.Symbol;
import top.vncnliu.event.server.mash.base.event.BaseEvent;
import top.vncnliu.event.server.mash.base.service.EntityService;
import top.vncnliu.event.server.mash.sample.store.aop.CacheSql;

/**
 * User: vncnliu
 * Date: 2018/7/27
 * Description:
 */
public interface ISymbolService extends EntityService<Symbol,Integer> {
    void insert();

    void insertSpring();

    @Transactional
    void insertSpring2();

    @CacheSql
    void testOut(BaseEvent baseEvent);
}
