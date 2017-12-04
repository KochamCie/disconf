package com.baidu.disconf.web.service.resource;

import lombok.Data;

/**
 * @author: hama
 * @date: created in  2017/11/19
 * @description:
 */
@Data
public class ResourceMapping {


    public String srcClassName;
    public String srcMethodName;
    public String srcMethod;
    public String srcUrl;
    public Class<?>[] srcMethodParamType;
    public String srcDescription;

    public ResourceMapping(String srcUrl, String srcMethod, String srcClassName, String srcMethodName,
                           Class<?>[] srcMethodParamType, String srcDescription) {
        this.srcUrl = srcUrl;
        this.srcMethod = srcMethod;
        this.srcClassName = srcClassName;
        this.srcMethodName = srcMethodName;
        this.srcMethodParamType = srcMethodParamType;
        this.srcDescription = srcDescription;
    }



}
