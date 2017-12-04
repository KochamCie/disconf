package com.baidu.disconf.web.service.roleres.dao.impl;

import com.baidu.disconf.web.common.Constants;
import com.baidu.dsp.common.dao.Columns;
import com.baidu.unbiz.common.genericdao.operator.Match;
import com.baidu.unbiz.common.genericdao.operator.Order;
import org.springframework.stereotype.Repository;

import com.baidu.disconf.web.service.roleres.bo.RoleResource;
import com.baidu.disconf.web.service.roleres.dao.RoleResourceDao;
import com.baidu.dsp.common.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weiwei
 * @date 2013-12-20 涓嬪崍6:35:04
 */
@Repository
public class RoleResourceDaoImpl extends AbstractDao<Integer, RoleResource> implements RoleResourceDao {

    /**
     * 获取角色的所有资源
     * @param roleId
     * @return
     */
    @Override
    public List<RoleResource> getRoleResource(int roleId) {
        List<Match> matchs = new ArrayList<Match>();
        matchs.add(new Match(Columns.ROLE_ID, roleId));
        return find(matchs, new ArrayList<Order>());
    }

}