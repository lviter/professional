# Hive

## 遇到的语法

### insert into 和insert overwrite区别

区别一

- insert into ：其实是将数据追加到表的末尾，注意：不是覆盖，是追加。
- insert overwrite:其实是将重写表（或分区）中的内容，即将原来的hive表（或分区）中的数据删除掉，再进行插入数据操作。（如果hive 表示分区表的话，insert overwrite
  操作只是会重写当前分区的数据，是不会重写其他分区的数据的。）

区别二

- hive > insert into dwd_user select * from ods_user; insert into 是可以省略table关键字
- hive > insert overwrite table dwd_user select * from ods_user; 覆盖之前的数据，table关键字不可省略

