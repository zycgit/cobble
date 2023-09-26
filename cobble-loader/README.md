# Cobble Loader

&emsp;&emsp; 资源的扫描/加载、加载器编排、类隔离/沙箱 工具框架。

## 功能和特性

- 资源加载：可以处理 classpath、目录、外部Jar/Zip、Jar in Jar
- 加载编排：可以处理不同加载器之间的导入、导出、聚合
- 类隔离/沙箱：
- 资源扫描：支持6中内置的匹配策略

## 内置扫描策略

- 内置 6 种资源匹配策略
    - None：扫描所有资源
    - ContainsAny 含有字符串
    - Match：全字匹配
    - Prefix：前缀匹配
    - Suffix：尾缀匹配
    - Regex：正则表达式匹配
- 支持自定义匹配策略
- 真对 classpath 支持扫描系统类，例如：rt.jar

## 加载器

- 基础加载器

| 类名 | 作用 |
| -------- | ------ |
| ClassPathResourceLoader | 处理 Classpath 中的资源加载 |
| JarResourceLoader | 外部独立的Jar 或 Jar in Jar |
| PathResourceLoader | 处理本地文件路径中的资源加载 |

- 加载器编排

| 类名 | 作用 |
| -------- | ------ |
| MultiResourceLoader | 简单的将多个 ResourceLoader 整合一起 |
| ExportResourceLoader | 对一个 ResourceLoader 提供资源访问白名单能力 |
| ImportResourceLoader | 按照导入规则将不同的 ResourceLoader 整合一起，相当于 ExportResourceLoader + MultiResourceLoader |
| IsolResourceLoader | 扩展自 ImportResourceLoader，提供类加载的隔离机制。例如：A.class -> C1.class, B.class -> C2.class，C1 和C2 是同名类不同版本 |

## 沙箱

How About Sandbox

- https://docs.oracle.com/javase/8/docs/technotes/guides/security/permissions.html

Policy Toos

- https://docs.oracle.com/javase/7/docs/technotes/guides/security/PolicyGuide.html

