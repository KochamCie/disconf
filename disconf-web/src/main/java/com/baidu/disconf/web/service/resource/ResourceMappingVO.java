package com.baidu.disconf.web.service.resource;

import lombok.Data;

/**
 * @author: hama
 * @date: created in  2017/11/19
 * @description:
 */
@Data
public class ResourceMappingVO{

    private String srcClassName;
    private String srcMethodName;
    private String srcMethod;
    private String srcUrl;
    private Class<?>[] srcMethodParamType;
    private String srcDescription;

    private boolean access;
    private String updateTime;
    private int roleResId;


    public ResourceMappingVO(ResourceMapping rm){
        if(null != rm){
            this.srcClassName = rm.getSrcClassName();
            this.srcMethodName = rm.getSrcMethodName();
            this.srcMethod = rm.getSrcMethod();
            this.srcUrl = rm.getSrcUrl();
            this.srcMethodParamType = rm.getSrcMethodParamType();
            this.srcDescription = rm.getSrcDescription();
        }
    }

    public ResourceMappingVO() {

    }
}
