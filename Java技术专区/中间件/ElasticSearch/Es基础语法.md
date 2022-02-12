## 示例

## 基本概念
Elasticsearch是面向文档型数据库，一条数据在这里就是一个文档，用JSON作为文档序列化的格式，如:
```json
{
    "name" :     "John",
    "sex" :      "Male",
    "age" :      25,
    "birthDate": "1990/05/01",
    "about" :    "I love to go rock climbing",
    "interests": [ "sports", "music" ]
}
```
es与关系型数据术语对照表：
```json
关系数据库      ⇒ 数据库        ⇒  表         ⇒ 行              ⇒ 列(Columns)
 
Elasticsearch  ⇒ 索引(Index)   ⇒ 类型(type)  ⇒ 文档(Docments)  ⇒ 字段(Fields)
```
一个es集群包含多个索引，包含很多类型。这些类型中包含很多的文档，每个文档包含很多的字段。ES的交互，可以使用JAVA API，也可以使用HTTP的restful API方式。

## 操作
1. 创建文档类型的索引
```es
PUT /website/blog/123
{
  "title": "My first blog entry",
  "text":  "Just trying this out...",
  "date":  "2014/01/01"
}
```

- website: 文档存放位置
- blog: 文档表示的对象类型
- 123: 文档唯一标识

2. 查询
```es
GET /website/blog/123?_source=title,text
```
3. 删除
```es
DELETE /website/blog/123
```
## 索引
ES索引精髓：
```
一切设计都是为了提高搜索的性能
```
