package com.baidu.disconf.web.web.spider.rili;

import com.baidu.disconf.web.service.spider.service.SpiderMgr;
import com.baidu.disconf.web.web.resource.ResourceController;
import com.baidu.dsp.common.annotation.NoAuth;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.vo.JsonObjectBase;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: hama
 * @date: created in  2017/12/25
 * @description:
 */

@Controller
@RequestMapping(WebConstants.API_PREFIX + "/spider")
public class SpiderController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private SpiderMgr spiderMgr;


    @NoAuth
    @RequestMapping(value = "/rili", method = RequestMethod.GET, name = "获取节假日列表")
    @ResponseBody
    public JsonObjectBase getList() {
        boolean result = false ;
        HtmlPage rootPage = spiderMgr.getPage();
        if(null == rootPage){
            return buildSuccess(result);
        }
        result = spiderMgr.handlerData(rootPage);
        return buildSuccess(result);
    }

}
