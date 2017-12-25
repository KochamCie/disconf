package com.baidu.disconf.web.service.spider;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.util.List;

/**
 * @author: hama
 * @date: created in  2017/12/25
 * @description:
 */
public class HolidaySpider {

    public static void main(String[] args) throws Exception {

        // 做的第一件事，去拿到这个网页，只需要调用getPage这个方法即可
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
        //设置webClient的相关参数
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        //webClient.getOptions().setTimeout(50000);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        //模拟浏览器打开一个目标网址
        HtmlPage rootPage = webClient.getPage("http://hao.360.cn/rili");
        System.out.println("为了获取js执行的数据 线程开始沉睡等待");
        Thread.sleep(3000);//主要是这个线程的等待 因为js加载也是需要时间的
        System.out.println("线程结束沉睡");
        String html = rootPage.asText();
        //System.out.println(html);
        DomNodeList<HtmlElement> lis = rootPage.getElementById("M-dates").getElementsByTagName("li");
        System.out.println("lis size :" + lis.size());

        List<String> list = HandlerData.createSql(HandlerData.getMonthInfo(rootPage));
        List<String> listXwb = HandlerData.createSqlForXwb(HandlerData.getMonthInfo(rootPage));
        for (String str : list
             ) {
            System.out.println(str);
        }

        for (String str : listXwb
                ) {
            System.out.println(str);
        }

        HtmlPage next = HandlerData.getNextPage(rootPage);
        System.out.println("=========================================================");
        list = HandlerData.createSql(HandlerData.getMonthInfo(next));
        for (String str : list
                ) {
            System.out.println(str);
        }
        listXwb = HandlerData.createSqlForXwb(HandlerData.getMonthInfo(next));
        for (String str : listXwb
                ) {
            System.out.println(str);
        }

        //
//        // 根据名字得到一个表单，查看上面这个网页的源代码可以发现表单的名字叫“f”
//        final HtmlForm form = htmlpage.getFormByName("f");
//        // 同样道理，获取”百度一下“这个按钮
//        final HtmlSubmitInput button = form.getInputByValue("百度一下");
//        // 得到搜索框
//        final HtmlTextInput textField = form.getInputByName("wd");
//        //搜索我的id
//        textField.setValueAttribute("th是个小屁孩");
//        // 输入好了，我们点一下这个按钮
//        final HtmlPage nextPage = button.click();
//        // 我把结果转成String
//        String result = nextPage.asXml();
//
//        System.out.println(result);  //得到的是点击后的网页

    }
}
