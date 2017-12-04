package com.baidu.disconf.web.service.config.dao.impl;

import com.baidu.disconf.web.common.Constants;
import com.baidu.disconf.web.service.config.form.ConfHistoryListForm;
import com.baidu.disconf.web.service.config.form.ConfListForm;
import com.baidu.dsp.common.dao.Columns;
import com.baidu.dsp.common.utils.DaoUtils;
import com.baidu.ub.common.db.DaoPage;
import com.baidu.ub.common.db.DaoPageResult;
import com.baidu.unbiz.common.genericdao.operator.Match;
import com.baidu.unbiz.common.genericdao.operator.Order;
import org.springframework.stereotype.Service;

import com.baidu.disconf.web.service.config.bo.ConfigHistory;
import com.baidu.disconf.web.service.config.dao.ConfigHistoryDao;
import com.baidu.dsp.common.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by knightliao on 15/12/25.
 */
@Service
public class ConfigHistoryDaoImpl extends AbstractDao<Long, ConfigHistory> implements ConfigHistoryDao {

    @Override
    public ConfigHistory getLastByConfigId(Long configId, int lastWhich) {
        List<ConfigHistory> list = getConfigHistoryList(configId);
        if(null != list && list.size()>lastWhich){
            return list.get(lastWhich);
        }
        return null;
    }

    @Override
    public List<ConfigHistory> getConfigHistoryList(Long configId) {
        List<Match> matchs = new ArrayList<Match>();
        matchs.add(new Match(Columns.CONFIG_ID, configId));
        List<Order> orders = new ArrayList<Order>();
        orders.add(new Order(Columns.CREATE_TIME, false));
        return find(matchs, orders);
    }

    @Override
    public DaoPageResult<ConfigHistory> getConfigHistoryList(Long configId, ConfHistoryListForm confHistoryListForm) {
        DaoPage daoPage = DaoUtils.daoPageAdapter(confHistoryListForm.getPage());
        System.out.println(daoPage.toString());
        List<Match> matchs = new ArrayList<Match>();
        matchs.add(new Match(Columns.CONFIG_ID, configId));
        return page2(matchs, daoPage);
    }
}
