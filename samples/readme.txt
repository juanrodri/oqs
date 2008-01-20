=======================================================
== Object Query System Samples                       ==
=======================================================
$Id$


1. BUILD AND RUN

  示例包含build.xml文件，可以用ant来执行。
  运行实例代码需要JDK1.5以上版本（OQS可以在JDK1.4.2上运行）。


2. 文件说明

  samples.properties		示例主配置文件
  oqs.properties		oqs相关配置
  applicationContext.xml	使用spring容器时必要的配置
  Utils：			用于读取配置文件的工具类。
  Samples：			示例的主要配置信息读取类。
  QueryFactoryFactory：		用编程的方式来生成QueryFactory，主要方法是getQueryFactory()。
  SpringBeanFactory：		获取spring的ApplicationContext的类。
  Main：			示例主程序。

  注意：使用Spring容器需要更多的类库，在发行包的lib或者samples的lib中可能并不包含这些LIB。



3. 更多详细信息请阅读《oqs开发参考手册》第四章：Object Query。



----------------------------------------------------------
Object Query System
Copyright 2006-2008 Alex Lin. All rights reserved.
Project Homepage (http://oqs.googlecode.com/).
