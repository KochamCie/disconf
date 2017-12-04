package com.baidu.disconf.web.service.resource;

import com.google.common.base.Enums;

/**
 * @author: hama
 * @date: created in  2017/11/20
 * @description: 文中几种使用到的restful风格的请求方式
 */
public enum MethodEnum {
    ELSE("0000"),
    GET("1000"),
    PUT("0100"),
    POST("0010"),
    DELETE("0001");

    private final String key;

    private MethodEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static MethodEnum getIfPresent(String name){
        return Enums.getIfPresent(MethodEnum.class, name).or(MethodEnum.ELSE);
    }
}
