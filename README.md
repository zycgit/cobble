# Cobble 工具包

&emsp;&emsp; cobble 译名:鹅卵石，是一种非常常见的小石头。通常用来当作建筑辅材以及景观布置。

分为 6 个部分（按字母序）：

- cobble-dynamic，基于 ASM 的 Java 动态代理、可平替 jdk 内置 Proxy类。
- cobble-lang，一些整理工具类和组建。
- cobble-loader，资源的扫描/加载、加载器编排、类隔离工具框架。
- cobble-settings，提供统一的配置文件访问接口，兼容 properties、xml、yaml 三种文件格式。

- MyBatis、Netty、Spring、SpringBoot、JDK8、Apache Commons(Lang3\BeanUtils\Collections\IO\Codec)

https://glassfish.dev.java.net/public/CDDL+GPL.html
http://www.apache.org/licenses/LICENSE-2.0

## release versions and prepare next SNAPSHOT version

    mvn release:clean release:prepare -Dmaven.test.skip -Prelease

## push to center maven repository (version tag must be RELEASE)

    mvn clean package install deploy -Prelease
