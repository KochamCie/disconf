package com.baidu.disconf.web.service.spider;

import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: hama
 * @date: created in  2017/12/25
 * @description:
 */
public class HandlerData {


    private static int index = 15922;

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 通过页面page调用分析方法获取到节假日列表
     *
     * @param page
     * @return
     * @throws ParseException
     */
    public static List<ChinaDate> getMonthInfo(HtmlPage page) throws ParseException {
        List<ChinaDate> dateList = new ArrayList<ChinaDate>();
        //p(page.asText());
        DomNodeList<HtmlElement> htmlElements = page.getElementById("M-dates").getElementsByTagName("li");

        return analysis(htmlElements, dateList);
    }



    /**
     * 通过htmlElements来分析出节假日列表
     *
     * @param htmlElements
     * @param dateList
     * @return
     * @throws ParseException
     */
    public static List analysis(DomNodeList<HtmlElement> htmlElements, List<ChinaDate> dateList) throws ParseException {
        for (HtmlElement element : htmlElements) {
            ChinaDate chinaDate = new ChinaDate();
            // 判断是不是crossmonth,跨月的不考虑
            if (element.getAttribute("class").indexOf("cross-month") != -1) {
                //p("****************************");
                continue;
            }
            List<HtmlElement> lunar = element.getElementsByAttribute("span", "class", "lunar");
            List<HtmlElement> solar = element.getElementsByAttribute("div", "class", "solar");
            chinaDate.setLunar(lunar.get(0).asText());
            chinaDate.setSolar(solar.get(0).asText());
            chinaDate.setSolarDate(dateFormat.parse(element.getAttribute("date")));
            if (element.getAttribute("class").indexOf("vacation") != -1) {
                // 有rest 或者  是 vacation样式的，则代表放假
                chinaDate.setVacation(true);
                chinaDate.setVacationName(getVocationName(htmlElements, element.getAttribute("date")));
            }
            if (element.getAttribute("class").indexOf("weekend") != -1 &&
                    element.getAttribute("class").indexOf("last") == -1) {
                // 判断是否是星期六,周末可能补班
                chinaDate.setSaturday(true);
            }
            if (element.getAttribute("class").indexOf("last weekend") != -1) {
                // 判断是否是星期天,周末可能补班
                chinaDate.setSunday(true);
            }
            if (element.getAttribute("class").indexOf("work") != -1) {
                // 有work样式的，就是补班了
                chinaDate.setWorkFlag(true);
            } else if (chinaDate.isSaturday() == false &&
                    chinaDate.isSunday() == false &&
                    chinaDate.isVacation() == false) {
                chinaDate.setWorkFlag(true);
            } else {
                chinaDate.setWorkFlag(false);
            }
            dateList.add(chinaDate);
        }
        return dateList;
    }


    /**
     * 拼接生成sql语句
     *
     * @param dateList
     * @return
     */
    public static List<String> createSql(List<ChinaDate> dateList) {
        List<String> sqlList = new ArrayList<String>();
        if (dateList.size() == 0) {
            sqlList.add("页面解析失败，无信息");
            return sqlList;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yy年MM月");
            String dateStr = sdf.format(dateList.get(0).getSolarDate());
            sqlList.add("1.ejb生产 [biz]库需要插入["+dateStr+"节假日]数据到[tb_holiday_record]表中");
        }

        for (ChinaDate date : dateList) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateStr = sdf.format(date.getSolarDate());
            String groupStr = dateStr.substring(0, 6);
            if (!date.isWorkFlag()) {
                sqlList.add("INSERT INTO `tb_holiday_record`(id,holiday,group_code) VALUES (" + dateStr + ", 1, " + groupStr + ");");
            } else {
                continue;
            }
        }
        return sqlList;
    }


    /**
     * 获取假期名称
     *
     * @param htmlElements
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getVocationName(DomNodeList<HtmlElement> htmlElements, String date) throws ParseException {
        String rst = "";
        boolean pastTimeFlag = false;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date paramDate = dateFormat.parse(date);
        if (new Date().getTime() >= paramDate.getTime()) {
            pastTimeFlag = true;
        }
//first step //jugde if can get vocation name from html page
        for (int i = 0; i < htmlElements.size(); i++) {
            HtmlElement element = htmlElements.get(i);
            if (element.getAttribute("class").indexOf("vacation") != -1) {
                boolean hitFlag = false;
                String voationName = "";
                for (; i < htmlElements.size(); i++) {
                    HtmlElement elementTmp = htmlElements.get(i);
                    String liDate = elementTmp.getAttribute("date");
                    List<HtmlElement> lunar = elementTmp.getElementsByAttribute("span", "class", "lunar");
                    String lanarText = lunar.get(0).asText();
                    if (lanarText.equals("元旦")) {
                        voationName = "元旦";
                    } else if (lanarText.equals("除夕") || lanarText.equals("春节")) {
                        voationName = "春节";
                    } else if (lanarText.equals("清明")) {
                        voationName = "清明";
                    } else if (lanarText.equals("国际劳动节")) {
                        voationName = "国际劳动节";
                    } else if (lanarText.equals("端午节")) {
                        voationName = "端午节";
                    } else if (lanarText.equals("中秋节")) {
                        voationName = "中秋节";
                    } else if (lanarText.equals("国庆节")) {
                        voationName = "国庆节";
                    }
                    if (liDate.equals(date)) {
                        hitFlag = true;
                    }
                    if (elementTmp.getAttribute("class").indexOf("vacation") == -1) {
                        break;
                    }
                }
                if (hitFlag == true && !voationName.equals("")) {
                    rst = voationName;
                    break;
                }
            } else {
                continue;
            }
        }
        return rst;
    }

    /**
     * 获取下一页
     * @param page
     * @return
     * @throws IOException
     */
    public static HtmlPage getNextPage(HtmlPage page) throws IOException {
        // 获取下一个月按钮

        DomNodeList<HtmlElement> hrefList = page.getElementById("M-controls").getElementsByTagName("a");
        return hrefList.get(3).click();
    }


    /**
     * 拼接生成希望宝sql语句
     *
     * @param dateList
     * @return
     */
    public static List<String> createSqlForXwb(List<ChinaDate> dateList) {
        List<String> sqlList = new ArrayList<String>();
        if (dateList.size() == 0) {
            sqlList.add("页面解析失败，无信息");
            return sqlList;
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yy年MM月");
            String dateStr = sdf.format(dateList.get(0).getSolarDate());
            sqlList.add("2.xwb_org生产 [xwb]库需要插入["+dateStr+"节假日]数据到[topenday]表中");
        }
        int i = index;
        for (ChinaDate date : dateList) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = sdf.format(date.getSolarDate());
            String groupStr = dateStr.replaceAll("-", "");
            if (!date.isWorkFlag()) {
                sqlList.add("INSERT INTO `topenday` VALUES ('"+(groupStr)+"', '"+dateStr+"', '0');");
            } else {
                sqlList.add("INSERT INTO `topenday` VALUES ('"+(groupStr)+"', '"+dateStr+"', '1');");
            }
        }
        return sqlList;
    }
}
