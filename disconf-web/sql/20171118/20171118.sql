-- 增加各表时间字段长度
-- app:create_time,update_time
ALTER TABLE `app`
MODIFY COLUMN `create_time` VARCHAR(19) NOT NULL;

ALTER TABLE `app`
MODIFY COLUMN `update_time` VARCHAR(19) NOT NULL;
-- config:create_time,update_time
ALTER TABLE `config`
MODIFY COLUMN `create_time` VARCHAR(19) NOT NULL;

ALTER TABLE `config`
MODIFY COLUMN `update_time` VARCHAR(19) NOT NULL;
-- config_history:create_time
ALTER TABLE `config_history`
MODIFY COLUMN `create_time` VARCHAR(19) NOT NULL;
-- env
-- role:create_time,update_time
ALTER TABLE `role`
MODIFY COLUMN `create_time` VARCHAR(19) NOT NULL;

ALTER TABLE `role`
MODIFY COLUMN `update_time` VARCHAR(19) NOT NULL;
-- role_resource:update_time
ALTER TABLE `role_resource`
MODIFY COLUMN `update_time` VARCHAR(19) NOT NULL;
-- user

