# 创建脚本
CREATE TABLE `t_user` (
`id` VARCHAR(50) NULL  ,
`creator` VARCHAR(50) NULL  ,
`gmt_created` DATETIME NULL ,
`modifier` VARCHAR(50) NULL  ,
`gmt_modified` DATETIME NULL ,
`name` VARCHAR(50) NULL  ,
`gender` CHAR(1) NULL  ,
`age` INT(4) NULL ,
`birthday` DATETIME NULL ,
`phone_number` VARCHAR(20) NULL  ,
`email` VARCHAR(150) NULL  
)
COLLATE='utf8_bin'
ENGINE=InnoDB;