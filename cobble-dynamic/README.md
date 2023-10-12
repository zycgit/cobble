# Cobble Dynamic

&emsp;&emsp; Cobble Dynamic 是一款基于 ASM 的 Java 动态代理工具，它最具特色的是可以通过装饰器模式为已有对象做增强。

## 功能和特性

- 支持 动态代理，针对类型做增强生成动态代理类(即Aop)
- 支持 对象代理，通过装饰模式为一个对象加装动态代理
- 支持 接口代理，让对象或类型动态实现一个或多个接口
- 支持 动态属性，给对象或类型设置动态属性
- 支持 兼容 JDK Proxy 原生 API

## 同类对比

| 特性           | Cobble | CGLIB | Java Proxy | AspectJ | Javassist |
|--------------|--------|-------|------------|---------|-----------|
| 动态代理         | ✅      | ✅     | ✅          | ✅       | ✅         |
| 接口代理         | ✅      | ✅     | ✅          | ❌       | ❌         |
| 动态属性         | ✅      | ✅     | ❌          | ❌       | ✅         |
| JDK Proxy 兼容 | ✅      | ✅     | 原生         | ❌       | ❌         |
| 对象代理         | ✅      | ❌     | ❌          | ❌       | ❌         |
| 难易程度         | 简单     | 简单    | 简单         | 中等（配置多） | 难（需要编码）   |
| 动态编译         | ❌      | ❌     | ❌          | ❌       | ✅         |

## 引入依赖

```xml
<dependency>
    <groupId>net.hasor</groupId>
    <artifactId>cobble-dynamic</artifactId>
    <version>4.6.1</version>
</dependency>
```

## 软件质量

- 行测试覆盖率：84%
- 有效代码行：1230
- 时间：2014年始，已有 10 年