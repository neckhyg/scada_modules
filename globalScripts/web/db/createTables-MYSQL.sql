--
--
create table globalScripts (
  id int not null auto_increment,
  xid varchar(50) not null,
  name varchar(100) not null,
  script longtext,
  primary key (id)
) engine=InnoDB;
alter table globalScripts add constraint globalScriptsUn1 unique (xid);
