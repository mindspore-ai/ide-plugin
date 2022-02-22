![MindSpore标志](https://gitee.com/mindspore/mindspore/raw/master/docs/MindSpore-logo.png "MindSpore logo")
[![LICENSE](https://img.shields.io/github/license/mindspore-ai/mindspore.svg?style=flat-square)](https://github.com/mindspore-ai/mindspore/blob/master/LICENSE)

<!-- TOC -->

* [MindSpore Dev Toolkit介绍](#mindspore-dev-toolkit介绍)

    * [MindSpore运行管理](#mindspore运行管理)
    * [对接智能知识搜索](#对接智能知识搜索)
    * [智能代码补全【TODO】](#智能代码补全todo)
    * [算子互搜【TODO】](#算子互搜todo)

* [安装及快速入门](#安装及快速入门)

    * [系统需求](#系统需求)
    * [安装](#安装)
    * [快速入门](#快速入门)
    * [源码编译安装](#源码编译安装)

* [源码构建](#源码构建)
* [社区](#社区)

    * [治理](#治理)
    * [交流](#交流)

* [贡献](#贡献)
* [版本说明](#版本说明)
* [许可证](#许可证)

<!-- /TOC -->

## MindSpore Dev ToolKit介绍

MindSpore Dev Toolkit是一款面向MindSpore开发者的开发套件。通过深度学习、智能搜索及智能推荐等技术，打造智能计算最佳体验，致力于全面提升MindSpore框架的易用性，助力MindSpore生态推广。
MindSpore Dev Toolkit提供如下功能：

### MindSpore运行管理

* 创建Conda环境或选择已有Conda环境，并安装MindSpore二进制包至Conda环境。
* 部署最佳实践模版。不仅可以测试环境是否安装成功，对新用户也提供了一个MindSpore的入门介绍。
* 在网络状况良好时，10分钟之内即可完成环境安装，开始体验MindSpore。最大可节约新用户80%的环境配置时间。

### 对接智能知识搜索

* 定向推荐：根据用户使用习惯，提供更精准的搜索结果。
* 沉浸式资料检索体检，避免在IDE和浏览器之间的互相切换。适配侧边栏，提供窄屏适配界面。

### 智能代码补全【TODO】

* 提供基于MindSpore项目的AI代码补全。
* 无需安装MindSpore环境，也可轻松开发MindSpore。

### 算子互搜【TODO】

* 快速搜索MindSpore算子，在侧边栏直接展示算子详情。
* 为方便其他机器学习框架用户，通过搜索其他主流框架算子，联想匹配对应MindSpore算子。

## 安装及快速入门

### 系统需求

MindSpore Dev ToolKit 是一款[PyCharm](https://www.jetbrains.com/pycharm/)插件。PyCharm是一款多平台Python IDE。

* 插件支持的操作系统：

    * Windows 10
    * Linux

* 插件支持的PyCharm版本:

    * 2020.3
    * 2021.1
    * 2021.2
    * 2021.3

### 安装

1. 获取[插件Zip包](https://ms-release.obs.cn-north-4.myhuaweicloud.com/1.6.0/IdePlugin/any/MindSpore_Dev_ToolKit-1.6.0.zip)。
2. 启动Pycharm单击左上菜单栏，选择File->Settings->Plugins->Install Plugin from Disk。
   如图：
   ![image-20211223175637989](./images/clip_image050.jpg)
3. 选择插件zip包。

### 快速入门

请参阅[快速入门](https://gitee.com/mindspore/ide-plugin/blob/master/MindSpore%20Dev%20Toolkit%20快速入门指南.md)。

### 源码编译安装

请参阅[源码构建](#源码构建)章节构建源码获取插件zip包，并参照[安装](#安装)章节安装。

## 源码构建

请参阅[编译指导](https://gitee.com/mindspore/ide-plugin/blob/master/MindSpore%20Dev%20ToolKit%20源码编译指导.md)。

## 社区

### 治理

查看MindSpore如何进行[开放治理](https://gitee.com/mindspore/community/blob/master/governance.md)。

### 交流

* [MindSpore Slack](https://join.slack.com/t/mindspore/shared_invite/zt-dgk65rli-3ex4xvS4wHX7UDmsQmfu8w) 开发者交流平台
* `#mindspore`IRC频道（仅用于会议记录）
* 视频会议：待定
* 邮件列表：<https://mailweb.mindspore.cn/postorius/lists>

## 贡献

欢迎参与贡献。更多详情，请参阅我们的[贡献者Wiki](https://gitee.com/mindspore/mindspore/blob/master/CONTRIBUTING.md)。

## 版本说明

1.6BETA开源

## 许可证

[Apache License 2.0](https://gitee.com/mindspore/mindspore#/mindspore/mindspore/blob/master/LICENSE)