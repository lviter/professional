# Apache cxf暴露接口以及客户端调用之WebService

[个人链接](https://www.cnblogs.com/javallh/p/9293162.html)

在我们真实的项目中，经常会调用别人提供给我们的接口，或者在自己的团队中，

restful风格的前后端分离也经常会提供一个后端接口暴露出去供app，或者.net/C/C++程序员去调用，此时就需要使用到一个工具或者一套程序来调用暴露的接口。

而今天我要说的就是其中的一个方式，使用apache的cxf调用以及暴露接口，让我们直接开始代码

1. 首先我们需要去下载cxf，cxf是apache的一个产品，下载链接附上：http://www.apache.org/dyn/closer.lua/cxf/3.2.5/apache-cxf-3.2.5.zip

