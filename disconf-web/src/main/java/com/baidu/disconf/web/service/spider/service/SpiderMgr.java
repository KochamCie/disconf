package com.baidu.disconf.web.service.spider.service;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.List;

/**
 * @author: hama
 * @date: created in  2017/12/25
 * @description:
 */
public interface SpiderMgr {

    public HtmlPage getPage();

    public HtmlPage getNextPage(HtmlPage parentPage);

    public boolean handlerData(HtmlPage page);


}
