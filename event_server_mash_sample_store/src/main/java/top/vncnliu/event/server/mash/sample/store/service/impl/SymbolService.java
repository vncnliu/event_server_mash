package top.vncnliu.event.server.mash.sample.store.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.vncnliu.event.server.mash.base.event.BaseEvent;
import top.vncnliu.event.server.mash.sample.store.aop.CacheSql;
import top.vncnliu.event.server.mash.sample.store.service.ISymbolService;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * User: vncnliu
 * Date: 2018/7/27
 * Description:
 */
@Service
public class SymbolService implements ISymbolService {

    private final EntityManager entityManager;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private DataSource dataSource;

    @Autowired
    public SymbolService(EntityManager entityManager, NamedParameterJdbcTemplate jdbcTemplate) {
        this.entityManager = entityManager;
        this.namedParameterJdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(){
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            String sql1 = "insert into event_mash_inventory.t_symbol (name) values ('苹果')";
            stmt.execute(sql1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @CacheSql
    @Override
    public void insertSpring(){
        //persist(new Symbol().setName("test"));
        //String sql1 = "insert into event_mash_inventory.t_symbol (name) values ('苹果')";
        String sql1 = "insert into event_mash_inventory.t_symbol (name) values ('苹果')";
        namedParameterJdbcTemplate.update(sql1, (Map<String, ?>) null);
        //insertSpring2();
    }

    @Transactional
    @CacheSql
    @Override
    public void insertSpring2(){
        String sql1 = "insert into event_mash_inventory.t_symbol (id,name) values (5,'苹果')";
        namedParameterJdbcTemplate.update(sql1, (Map<String, ?>) null);
    }

    @CacheSql
    @Override
    public void testOut(BaseEvent baseEvent){
        System.out.println("in service");
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
