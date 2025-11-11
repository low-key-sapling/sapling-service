---
inclusion: manual
---
# 项目结构指南

这是一个基于 Spring Boot 的多模块项目，主要包含以下模块：

- [endpoint-server](mdc:endpoint-server): 主服务器模块，包含应用程序入口点
- [endpoint-framework](mdc:endpoint-framework): 框架核心模块
- [endpoint-dependencies](mdc:endpoint-dependencies): 依赖管理模块
- [endpoint-module-system](mdc:endpoint-module-system): 系统功能模块

## 关键文件

- [pom.xml](mdc:pom.xml): 项目主 POM 文件，定义了项目的基本信息和模块结构
- [README.md](mdc:README.md): 项目说明文档，包含迭代计划和功能列表

## 构建输出

项目构建后会在 `target` 目录下生成以下结构：
- `/bin`: 启动脚本
- `/config`: 配置文件
- `/lib`: 依赖库
- `/jks`: 证书文件

