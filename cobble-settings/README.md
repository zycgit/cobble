# Cobble Settings

&emsp;&emsp; 提供统一的配置文件访问接口，同时兼容多种文件格式。

## 能力

- 支持 properties、xml、yaml 三种格式配置文件
- 支持同时加载多个配置文件，配置遇到冲突时候后面会覆盖前面的

## 用法

```java
Settings settings = new DefaultSettings("net_hasor_core_settings/value-config.xml");

String myName = settings.getString("mySelf.myName");

```

## 引入依赖

```xml
<dependency>
    <groupId>net.hasor</groupId>
    <artifactId>cobble-settings</artifactId>
    <version>4.6.1</version>
</dependency>
```

## 软件质量

- 行测试覆盖率：81%
- 有效代码行：808