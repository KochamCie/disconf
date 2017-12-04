package com.baidu.disconf.web.web.role;

import com.baidu.disconf.web.service.role.bo.Role;
import com.baidu.disconf.web.service.role.service.RoleMgr;
import com.baidu.disconf.web.web.resource.ResourceController;
import com.baidu.dsp.common.annotation.NoAuth;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.vo.JsonObjectBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author: hama
 * @date: created in  2017/11/20
 * @description:
 */
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/role")
public class RoleController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleMgr roleMgr;

    @NoAuth
    @RequestMapping(value = "/list", method = RequestMethod.GET, name = "获取role列表")
    @ResponseBody
    public JsonObjectBase getRoleList() {
        List<Role> list = roleMgr.findAll();
        return buildListSuccess(list, list.size());
    }


}
