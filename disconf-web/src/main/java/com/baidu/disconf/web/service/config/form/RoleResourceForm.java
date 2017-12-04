package com.baidu.disconf.web.service.config.form;

import com.baidu.dsp.common.form.RequestFormBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * hama
 */
@Data
public class RoleResourceForm extends RequestFormBase {

    /**
     *
     */
    private static final long serialVersionUID = 4556205917734852359L;

    @NotNull
    private int roleId;

    @NotNull
    private String urlPattern;

    @NotNull
    private String methodMask;

    @NotNull
    private String urlDescription;



}
