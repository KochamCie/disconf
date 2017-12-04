package com.baidu.disconf.web.service.config.form;

import com.baidu.dsp.common.form.RequestListBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author liaoqiqi
 * @version 2014-6-23
 */
@Data
public class ConfHistoryListForm extends RequestListBase {

    /**
     *
     */
    private static final long serialVersionUID = -2498128894396346299L;

    private Long appId;

    private String version;

    private Long envId;

    private int pageNo = 0;

    private int pageSize = 99999999;

}
