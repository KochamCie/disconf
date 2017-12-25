package com.baidu.disconf.web.service.spider.service.impl;

import com.baidu.disconf.web.service.roleres.service.impl.RoleResourceMgrImpl;
import com.baidu.disconf.web.service.spider.HandlerData;
import com.baidu.disconf.web.service.spider.service.SpiderMgr;
import com.baidu.disconf.web.utils.CodeUtils;
import com.baidu.disconf.web.utils.DiffUtils;
import com.baidu.dsp.common.utils.email.LogMailBean;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author: hama
 * @date: created in  2017/12/25
 * @description:
 */

@Service
public class SpiderMgrImpl implements SpiderMgr {

    protected static final Logger LOG = LoggerFactory.getLogger(RoleResourceMgrImpl.class);


    @Autowired
    private LogMailBean logMailBean;



    @Override
    public HtmlPage getPage() {
        HtmlPage page = null;
        // 做的第一件事，去拿到这个网页，只需要调用getPage这个方法即可
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
        //设置webClient的相关参数
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        //webClient.getOptions().setTimeout(50000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        //模拟浏览器打开一个目标网址
        try {
            page = webClient.getPage("http://hao.360.cn/rili");
        } catch (IOException e) {
            e.printStackTrace();
            return page;
        }
        LOG.info("为了获取js执行的数据 线程开始沉睡等待");
        try {
            Thread.sleep(3000);//主要是这个线程的等待 因为js加载也是需要时间的
        } catch (InterruptedException e) {
            e.printStackTrace();
            return page;
        }
        LOG.info("线程结束沉睡");
        return page;
    }

    @Override
    public HtmlPage getNextPage(HtmlPage parentPage) {
        return null;
    }

    @Override
    public boolean handlerData(HtmlPage page) {

        // 获取判断当月是否有数据
        DomNodeList<HtmlElement> lis = page.getElementById("M-dates").getElementsByTagName("li");
        System.out.println("lis size :" + lis.size());
        if(lis.size()<10){
            return false;
        }

        // get next page
        HtmlPage next = null;
        try {
            next = HandlerData.getNextPage(page);
            LOG.info("=========================================================");
            List<String> list = HandlerData.createSql(HandlerData.getMonthInfo(next));
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < list.size(); i++) {
                LOG.info(list.get(i));
                sb.append(list.get(i)+"<br>");
                if(i==0){
                    sb.append("<br>");
                }
            }

            sb.append("<br>");
            sb.append("<br>");
            sb.append("<br>");
            List<String> listXwb = HandlerData.createSqlForXwb(HandlerData.getMonthInfo(next));
            int i=0;
            for (String str : listXwb
                    ) {
                LOG.info(str);
                sb.append(str+"<br>");
                if(i==0){
                    sb.append("<br>");
                }
                i++;
            }

            // send email

            String sendTo = "renns@newhope.cn;gaogj@newhope.cn;tengpf@newhope.cn";
            Date now = new Date();
            // 跨年添加情况
            int month = (now.getMonth()+1)==12?1:now.getMonth()+1;
            int year  = (now.getMonth()+1)==12?now.getYear()+1901:now.getYear()+1900;


            boolean isSendSuccess = logMailBean.sendHtmlEmail(sendTo,"新增["+year+"年"+month+"月]节假日数据", sb.toString());
            if (isSendSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
