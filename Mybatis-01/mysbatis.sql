#drop database mybatis;

##创建mybatis依赖的数据库
create database mybatis;

use mybatis;

##创建用户表
create table user
(
    id   int(20) not null primary key,
    name varchar(30) default null,
    pwd  varchar(30) default null
) ENGINE = INNODB
  default char set = utf8;

##插入用户数据
insert into user
values (1, 'wyl', '888888'),
       (2, 'bsy', '888888'),
       (3, 'zhazha', '000000');
