--
--
create table globalScripts (
  id int not null identity,
  xid nvarchar(50) not null,
  name nvarchar(100) not null,
  script ntext,
  primary key (id)
);
alter table globalScripts add constraint globalScriptsUn1 unique (xid);
