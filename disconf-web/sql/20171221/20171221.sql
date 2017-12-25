-- config表中增加auto_reload,java_client字段
alter TABLE config ADD COLUMN java_client TINYINT(1) DEFAULT 0 NOT NULL;
alter TABLE config ADD COLUMN auto_reload TINYINT(1) DEFAULT 0 NOT NULL;
