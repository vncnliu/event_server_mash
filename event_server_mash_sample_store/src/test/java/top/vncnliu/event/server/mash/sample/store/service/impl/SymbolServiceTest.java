package top.vncnliu.event.server.mash.sample.store.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import top.vncnliu.event.server.mash.base.entity.Symbol;
import top.vncnliu.event.server.mash.base.service.ISymbolService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SymbolServiceTest {

    @Autowired
    private ISymbolService symbolService;

    @Transactional
    @Test
    void main(){
        symbolService.persist(new Symbol().setName("test"));
    }
}