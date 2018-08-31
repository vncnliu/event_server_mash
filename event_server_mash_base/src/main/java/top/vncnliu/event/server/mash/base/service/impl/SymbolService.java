package top.vncnliu.event.server.mash.base.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import top.vncnliu.event.server.mash.base.Constant;
import top.vncnliu.event.server.mash.base.entity.EventTask;
import top.vncnliu.event.server.mash.base.service.IEventTaskService;
import top.vncnliu.event.server.mash.base.service.ISymbolService;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
    public SymbolService(EntityManager entityManager, NamedParameterJdbcTemplate jdbcTemplate) {
        this.entityManager = entityManager;
        this.namedParameterJdbcTemplate = jdbcTemplate;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
