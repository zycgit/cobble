# Cobble Lang

## 常用 Utils

| Util 名称 | 作用 |
| -------- | ------ |
| ArrayUtils | 对数组的常规操作，比如：add、remove、clone、contains、indexOf、isEmpty、reverse、subarray等 |
| BeanUtils | 提供 Bean 相关的一些工具，比如：字段/属性的读写、bean 拷贝 |
| BitUtils | 二进制位 check |
| BooleanUtils | boolean 类型值的一些工具 |
| ByteUtils | |
| CharUtils | |
| ClassUtils | |
| CollectionUtils | |
| ConstructorUtils | |
| ConverterUtils | |
| ExceptionUtils | |
| FieldUtils | |
| FilenameUtils | |
| FileUtils | |
| IOUtils | |
| MatchUtils | |
| MemberUtils | |
| MethodUtils | |
| NetworkUtils | |
| NumberUtils | |
| ObjectUtils | |
| RandomUtils | 随机生成 boolean、bytes、double、float、int、long |
| ResolvableType | 泛化类型信息提取工具 |
| ResourcesUtils | |
| StringEscapeUtils | |
| StringUtils | |
| SystemUtils | |

## IO Streams

| 类名 | 作用 |
| -------- | ------ |
| Base64InputStream、Base64OutputStream | |
| AutoCloseInputStream | |
| ClosedInputStream | |
| ReaderInputStream | |
| Base64OutputStream | |
| ByteArrayOutputStream | |
| DeferredFileOutputStream | |
| StringBuilderWriter | |
| ThresholdingOutputStream | |
| WriterOutputStream | |

## IO 黑洞系列

| 类名 | 作用 |
| -------- | ------ |
| BlackHoleAppendable、BlackHoleDataOutput、BlackHoleObjectOutput、BlackHoleOutputStream、BlackHolePrintWriter、BlackHoleWriter | IO黑洞 |

## 工具类

| 类名 | 作用 |
| -------- | ------ |
| NameThreadFactory | 线程工厂 |
| QoSBucket | 基于令牌桶的限流器 |
| HashedWheelTimer | 基于时间轮的单线程 Timer 定时器 |
| BasicFuture | |
| BeanMap | 将一个 Bean 转换为 Map 对象 |
| WeakArrayList | 弱引用 ArrayList |
| WeakHashSet | 弱引用 HashSet |
| SpaceHashMap | 将多个 Map 合并到一起，并可以为每一个 Map 设置一个 Space。并根据 Space 单独操作 |
| SimpleLRU | |
| LinkedCaseInsensitiveMap | Key为 String 并且忽略大小写 |
| Charsets | |
| LineIterator | |
| DateFormatType | |
| RandomFile | 随机方式读写文件 |

## 小组建

| 类名 | 作用 |
| -------- | ------ |
| scope【Prototype、Singleton、Chain】 | |
| supplier | |

## 辅助接口和类

| 类名 | 作用 |
| -------- | ------ |
| SFunction | |
| TypeReference | |
| EConsumer | |
| EFunction | |
| ESupplier | |
