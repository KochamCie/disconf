package com.baidu.disconf.web.web.config.controller;

import javax.validation.constraints.NotNull;

import com.baidu.disconf.web.service.config.bo.ConfigHistory;
import com.baidu.disconf.web.service.config.service.ConfigHistoryMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.web.config.validator.ConfigValidator;
import com.baidu.disconf.web.web.config.validator.FileUploadValidator;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.controller.BaseController;
import com.baidu.dsp.common.exception.FileUploadException;
import com.baidu.dsp.common.vo.JsonObjectBase;

import java.util.List;

/**
 * 专用于配置更新、删除
 *
 * @author liaoqiqi
 * @version 2014-6-24
 */
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/web/config")
public class ConfigUpdateController extends BaseController {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigUpdateController.class);

    @Autowired
    private ConfigMgr configMgr;

    @Autowired
    private ConfigValidator configValidator;

    @Autowired
    private FileUploadValidator fileUploadValidator;

    @Autowired
    private ConfigHistoryMgr configHistoryMgr;

    /**
     * 配置项的更新
     *
     * @param configId
     * @param value
     * @return
     */
    @RequestMapping(value = "/item/{configId}", method = RequestMethod.PUT, name = "配置项的更新")
    @ResponseBody
    public JsonObjectBase updateItem(@PathVariable long configId, String value) {

        // 业务校验
        configValidator.validateUpdateItem(configId, value);

        LOG.info("start to update config: " + configId);

        //
        // 更新, 并写入数据库
        //
        String emailNotification = "";
        emailNotification = configMgr.updateItemValue(configId, value);

        //
        // 通知ZK
        //
        configMgr.notifyZookeeper(configId);

        return buildSuccess(emailNotification);
    }

    /**
     * 配置文件的更新
     *
     * @param configId
     * @param file
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/file/{configId}", method = RequestMethod.POST, name = "配置文件的更新")
    public JsonObjectBase updateFile(@PathVariable long configId, @RequestParam("myfilerar") MultipartFile file) {

        //
        // 校验
        //
        int fileSize = 1024 * 1024 * 4;
        String[] allowExtName = {".properties", ".xml"};
        fileUploadValidator.validateFile(file, fileSize, allowExtName);

        // 业务校验
        configValidator.validateUpdateFile(configId, file.getOriginalFilename());

        //
        // 更新
        //
        String emailNotification = "";
        try {

            String str = new String(file.getBytes(), "UTF-8");
            LOG.info("receive file: " + str);

            emailNotification = configMgr.updateItemValue(configId, str);
            LOG.info("update " + configId + " ok");

        } catch (Exception e) {

            LOG.error(e.toString());
            throw new FileUploadException("upload file error", e);
        }

        //
        // 通知ZK
        //
        configMgr.notifyZookeeper(configId);

        return buildSuccess(emailNotification);
    }

    /**
     * 配置文件的更新(文本修改)
     *
     * @param configId
     * @param fileContent
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/filetext/{configId}", method = RequestMethod.PUT, name = "配置文件的更新(文本修改)")
    public JsonObjectBase updateFileWithText(@PathVariable long configId, @NotNull String fileContent) {

        //
        // 更新
        //
        String emailNotification = "";
        try {

            String str = new String(fileContent.getBytes(), "UTF-8");
            LOG.info("receive file: " + str);

            emailNotification = configMgr.updateItemValue(configId, str);
            LOG.info("update " + configId + " ok");

        } catch (Exception e) {

            throw new FileUploadException("upload.file.error", e);
        }

        //
        // 通知ZK
        //
        configMgr.notifyZookeeper(configId);

        return buildSuccess(emailNotification);
    }

    /**
     * delete
     *
     * @return
     */
    @RequestMapping(value = "/{configId}", method = RequestMethod.DELETE, name = "删除配置文件")
    @ResponseBody
    public JsonObjectBase delete(@PathVariable long configId) {

        configValidator.validateDelete(configId);

        configMgr.delete(configId);

        return buildSuccess("删除成功");
    }

    /**
     * 回滚配置文件内容至上一个版本
     *
     * @param configId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/rollback/{configId}", method = RequestMethod.PUT, name = "回滚配置文件内容至上一个版本")
    public JsonObjectBase updateLastConfig(@PathVariable long configId) {

        // 获取上一个版本的内容，从config_history中获取
        ConfigHistory ch = configHistoryMgr.getLastByConfigId(configId, 1);
        String emailNotification = "";
        try {
            emailNotification = configMgr.updateItemValue(configId, ch.getNewValue());
            LOG.info("update " + configId + " ok");
        } catch (Exception e) {
            throw new FileUploadException("upload.file.error", e);
        }
        //
        // 通知ZK
        //
        // configMgr.notifyZookeeper(configId);

        return buildSuccess(emailNotification);
    }

}
