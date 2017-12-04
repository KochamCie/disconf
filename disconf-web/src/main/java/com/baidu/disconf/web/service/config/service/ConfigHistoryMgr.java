package com.baidu.disconf.web.service.config.service;

import com.baidu.disconf.web.service.config.bo.ConfigHistory;
import com.baidu.disconf.web.service.config.form.ConfHistoryListForm;
import com.baidu.ub.common.db.DaoPageResult;

import java.util.List;

/**
 * Created by knightliao on 15/12/25.
 */
public interface ConfigHistoryMgr {

    void createOne(Long configId, String oldValue, String newValue);

    ConfigHistory getLastByConfigId(Long configId, int lastWhich);

    List<ConfigHistory> getConfigHistoryList(Long configId);

    DaoPageResult<ConfigHistory> getConfigHistoryList(Long configId, ConfHistoryListForm confHistoryListForm);

}
