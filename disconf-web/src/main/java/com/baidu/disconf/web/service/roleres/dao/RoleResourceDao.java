package com.baidu.disconf.web.service.roleres.dao;

import com.baidu.disconf.web.service.roleres.bo.RoleResource;
import com.baidu.unbiz.common.genericdao.dao.BaseDao;

import java.util.List;

/**
 * @author weiwei
 * @date 2013-12-20 涓嬪崍6:16:31
 */
public interface RoleResourceDao extends BaseDao<Integer, RoleResource> {

    /**
     * 获取角色的所有资源
     * @param roleId
     * @return
     */
    List<RoleResource> getRoleResource(int roleId);

}
