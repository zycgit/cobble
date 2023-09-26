# Cobble Lang

## 常规 Utils

| Util 名称           | 作用                                                                      |
|-------------------|-------------------------------------------------------------------------|
| ArrayUtils        | 对数组的常规操作，比如：add、remove、clone、contains、indexOf、isEmpty、reverse、subarray等 |
| BeanUtils         | 提供 Bean 相关的一些工具，比如：字段/属性的读写、bean 拷贝                                     |
| BitUtils          | 二进制位 check                                                              |
| BooleanUtils      | boolean 类型值的一些工具                                                        |
| ByteUtils         |                                                                         |
| CharUtils         |                                                                         |
| CollectionUtils   |                                                                         |
| ConstructorUtils  |                                                                         |
| ConverterUtils    |                                                                         |
| ExceptionUtils    |                                                                         |
| MatchUtils        |                                                                         |
| NetworkUtils      |                                                                         |
| NumberUtils       |                                                                         |
| ObjectUtils       |                                                                         |
| RandomUtils       | 随机生成 boolean、bytes、double、float、int、long                                |
| ResolvableType    | 泛化类型信息提取工具                                                              |
| ResourcesUtils    |                                                                         |
| StringEscapeUtils |                                                                         |
| StringUtils       |                                                                         |
| SystemUtils       |                                                                         |

## 类和反射

| 类名                | 作用  |
|-------------------|-----|
| MethodUtils       |     |
| ClassUtils        |     |
| FieldUtils        |     |
| ConstructorUtils  |     |
| FieldUtils        |     |
| MemberUtils       |     |
| MethodUtils       |     |
| TypeReference     |     |
| ResolvableType    |     |
| SFunction         |     |

## IO 流

| 类名                                    | 作用                                                                                     |
|---------------------------------------|----------------------------------------------------------------------------------------|
| Base64InputStream、Base64OutputStream  | 提供 Base64 编码的输入/输出流                                                                    |
| AutoCloseReader、 AutoCloseInputStream | 流读取到末尾后会自动调用 close 方法                                                                  |
| ClosedReader、ClosedInputStream        | 一个永久处于 close 状态的 Reader/InputStream 读取到的数据永远为 -1                                       |
| ReaderInputStream                     | Reader 转换为 InputStream                                                                 |
| WriterOutputStream                    | Writer 转换为 OutputStream                                                                |
| ByteArrayOutputStream                 | 增强的 ByteArrayOutputStream 在扩容的时候不会产生数据拷贝                                               |
| DeferredFileOutputStream              | 一个带有缓存的文件输出流，数据会写入到内存，只有当达到一定的阀值后数据才会写入磁盘。这个类起源于 FileUpload 当文件很小的时候上传数据处于内存可以加速 IO 速度 |
| StringBuilderWriter                   | 将 StringBuilder 转换为 Writer                                                             |
| ThresholdingOutputStream              | 当写入指定字节数的数据时触发事件的输出流。例如，可以使用该事件在达到最大值时抛出异常，或者在超过阈值时切换基础流类型。                            |
| ReversedLinesFileReader               | 反向读取文件中的行(类似于BufferedReader，但从文件的最后一行开始)。例如，在日志文件中搜索。                                  |
| BlackHoleAppendable                   | Appendable 黑洞，可以写入任意多数据，被写入的数据会全部被丢弃                                                   |
| BlackHoleDataOutput                   | DataOutput 黑洞，可以写入任意多数据，被写入的数据会全部被丢弃                                                   |
| BlackHoleObjectOutput                 | ObjectOutput 黑洞，可以写入任意多数据，被写入的数据会全部被丢弃                                                 |
| BlackHoleOutputStream                 | OutputStream 黑洞，可以写入任意多数据，被写入的数据会全部被丢弃                                                 |
| BlackHolePrintWriter                  | PrintWriter 黑洞，可以写入任意多数据，被写入的数据会全部被丢弃                                                  |
| BlackHoleWriter                       | Writer 黑洞，可以写入任意多数据，被写入的数据会全部被丢弃                                                       |

## 文件

| 类名            | 作用                       |
|---------------|--------------------------|
| FilenameUtils |                          |
| FileUtils     |                          |
| IOUtils       |                          |
| LineIterator  | 以 Iterator 接口形式一行一行的访问文件 |
| RandomFile    | 随机方式读写文件                 |

## 编码/解码

| 类名       | 作用  |
|----------|-----|
| Base64   |     |
| HexUtils |     |
| MD5      |     |

## 并发与多线程

| 类名               | 作用                                                           |
|------------------|--------------------------------------------------------------|
| Future           | java Future 的增强，可以取消任务、以及监听 cancel、completed、failed、final 事件 |
| HashedWheelTimer | 基于时间轮的 Timer，该类源于 Netty                                      |
| QoSBucket        | 基于令牌桶的限流器                                                    |
| ThreadUtils      |                                                              |
| TimeUnit         | 用于表示时间单位，该类源于 JDK 1.8                                        |

## 数据结构

| 类名                         | 作用                                                                     |
|----------------------------|------------------------------------------------------------------------|
| BeanMap                    | 将对象转换为 Map 结构，读写 Map 会反射的方式读写 Bean                                     |
| BufferedIterator           | 使用 BufferedIterator 允许迭代器 mark 一个位置。 并在允许的缓冲范围内随时回退到 mark 位置上。         |
| ConcurrentReferenceHashMap | 键和值都是弱引用的 ConcurrentHashMap，该类源于 Spring                                |
| ContextClassLoaderLocal    | 与 ThreadLocal 的工作原理类似，不同的是 Key 绑定到当前线程的ClassLoader。该类源于 Apache Commons |
| IntHashMap                 | 使用 Int 作为 Key时，相比较使用 HashMap 会节省大量内存并提升效率                              |
| LinkedCaseInsensitiveMap   | LinkedHashMap变体，以不区分大小写的方式存储字符串键，例如在结果表中基于键的访问。该类源于 Spring JDBC        |
| SimpleLRU                  | 基于 LinkedHashMap 的 LRU 实现                                              |
| SpaceHashMap               | 可以将多个 Map 合并进一个 Map，同时避免 Map 之间的数据拷贝和覆盖。其数据结构为：Map<String, Map<K, T>>  |
| WeakArrayList              | 弱引用 ArrayList，该类源于 org.arakhne.util.ref                                |
| WeakHashSet                | 基于 WeakHashMap 的弱引用 HashSet 实现                                         |

## 工具类

| 类名                       | 作用                                                            |
|--------------------------|---------------------------------------------------------------|
| BeanMap                  | 将一个 Bean 转换为 Map 对象                                           |
| WeakArrayList            | 弱引用 ArrayList                                                 |
| WeakHashSet              | 弱引用 HashSet                                                   |
| SpaceHashMap             | 将多个 Map 合并到一起，并可以为每一个 Map 设置一个 Space。并根据 Space 单独操作           |
| LinkedCaseInsensitiveMap | Key为 String 并且忽略大小写                                           |
| Charsets                 | 定义了若干字符集，如：US-ASCII、ISO-8859-1、UTF-8、UTF-16BE、UTF-16LE、UTF-16 |
| DateFormatType           | 识别字符串时间的格式                                                    |
| WellKnowFormat           | 时间日期格式                                                        |
| I18nUtils                | I18n 国际化资源加载和获取                                               |
| RandomRatio              | 提供一种随机机制；可以设定一组对象，同时为每个对象设定一个随机出现概率。在使用过程中可以按照约定的概率随机抽取它们。    |
| EventBus                 | 事件总线，用于逻辑解耦；例如：本地多模块之间通讯。可同步、可异步                              |

## 作用域

| 类名                   | 作用                                        |
|----------------------|-------------------------------------------|
| Provider             | 汇聚了 Supplier, Callable 两个接口的实现，并提供了一些工具方法 |
| Scope                | 作用域及作用域链；可以方便构建多级缓存、或多级 Factory           |
| InstanceProvider     | 将一个具体对象封装为 Provider                       |
| LoaderSingleProvider | 基于 ContextClassLoaderLocal 的单例            |
| SingleProvider       | 单例                                        |
| ThreadSingleProvider | 基于 ThreadLocal 的单例                        |
| PrototypeScope       | 原型作用域，与 SingletonScope 作用相反               |
| SingletonScope       | 单例作用域，与 PrototypeScope 作用相反               |

## 辅助类/接口

| 类名        | 作用  |
|-----------|-----|
| EConsumer |     |
| EFunction |     |
| ESupplier |     |
| Property  |     |
| Range     | 表示 A～B 的一个范围                                                  |
