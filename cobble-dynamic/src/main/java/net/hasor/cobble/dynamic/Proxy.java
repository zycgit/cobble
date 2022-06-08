/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble.dynamic;
import net.hasor.cobble.ExceptionUtils;
import net.hasor.cobble.StringUtils;
import net.hasor.cobble.dynamic.DynamicConfig.DynamicPropertyInfo;
import org.objectweb.asm.Type;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.*;

/**
 *
 * @version : 2022-06-05
 * @author 赵永春 (zyc@hasor.net)
 */
public final class Proxy {
    private static final AtomicLong spinIndex       = new AtomicLong(0);
    private static final String     aopClassSuffix  = "$proxy$";
    private static final String     aopMethodSuffix = "redirect$";

    private static final JdkWeakCache<ClassLoader, DynamicConfig, Class<?>> proxyClassCache   = new JdkWeakCache<>(new KeyFactory(), new ProxyClassFactory());
    private static final JdkWeakCache<ClassLoader, DynamicConfig, Class<?>> dynamicClassCache = new JdkWeakCache<>(new KeyFactory(), new DynamicClassFactory());
    private static final Method                                             defineClassMethod;

    private static final class KeyFactory implements BiFunction<ClassLoader, DynamicConfig, Object> {
        @Override
        public Object apply(ClassLoader classLoader, DynamicConfig dynamicConfig) {
            return new Key(dynamicConfig);
        }
    }

    /* a key used for proxy class with any number of implemented interfaces (used here for 3 or more only) */
    private static final class Key extends WeakReference<DynamicConfig> {
        private final int hash;

        Key(DynamicConfig config) {
            super(config);
            this.hash = config.hashCode();
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            DynamicConfig intf;
            return this == obj || //
                    obj != null && obj.getClass() == Key.class && (intf = get()) != null && intf == ((Key) obj).get();
        }
    }

    private static class ProxyInvocationException extends RuntimeException {

        public ProxyInvocationException(Exception e) {
            super(e.getMessage(), e);
        }
    }

    private static final class ProxyClassFactory implements BiFunction<ClassLoader, DynamicConfig, Class<?>> {

        @Override
        public Class<?> apply(ClassLoader classLoader, DynamicConfig config) {
            try {
                return buildAndLoadClass(config, true, classLoader);
            } catch (Exception e) {
                throw new ProxyInvocationException(e);
            }
        }
    }

    private static final class DynamicClassFactory implements BiFunction<ClassLoader, DynamicConfig, Class<?>> {

        @Override
        public Class<?> apply(ClassLoader classLoader, DynamicConfig config) {
            try {
                return buildAndLoadClass(config, false, classLoader);
            } catch (Exception e) {
                throw new ProxyInvocationException(e);
            }
        }
    }

    static {
        try {
            defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            defineClassMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    // --------------------------------------------------------------------------------------------------------------------------

    private static Class<?> realSuperType(Class<?> superType) {
        if (isDynamicClass(superType)) {
            return getPrototypeType(superType);
        } else {
            return superType;
        }
    }

    /** 兼容 jdk Proxy 用法 */
    public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler handler) throws ReflectiveOperationException {
        return newProxyClass(loader, interfaces, handler).newInstance();
    }

    /** 兼容 jdk Proxy 用法 */
    public static Class<?> newProxyClass(ClassLoader loader, Class<?>[] interfaces, InvocationHandler handler) {
        if (interfaces.length == 0 || handler == null) {
            throw new IllegalStateException("no interfaces or handler is specified.");
        }

        Class<?> superClass = null;
        for (Class<?> inter : interfaces) {
            if (!inter.isInterface()) {
                if (superClass != null) {
                    throw new IllegalArgumentException("only one base class is allowed.");
                } else {
                    superClass = inter;
                }
            }
        }

        DynamicConfig config = new DynamicConfig(superClass, handler);
        for (Class<?> inter : interfaces) {
            if (superClass == inter) {
                continue; // is super class
            }
            if (!inter.isInterface()) {
                throw new IllegalArgumentException("interfaces type " + inter + " is not interface.");
            }
            config.addImplements(inter, handler);
        }

        return newEnhanceClass(loader, config);
    }

    /** 创建对象的代理对象 */
    public static <T> T newEnhanceInstance(ClassLoader loader, T instance, Predicate<Method> methodMatcher, MethodInterceptor... interceptors) throws ReflectiveOperationException {
        Objects.requireNonNull(instance, "instance is null.");
        Objects.requireNonNull(methodMatcher, "methodMatcher is null.");
        if (interceptors == null || interceptors.length == 0) {
            return instance;
        }

        DynamicConfig config = new DynamicConfig(realSuperType(instance.getClass()));
        for (MethodInterceptor interceptor : interceptors) {
            config.addAopInterceptor(methodMatcher, interceptor);
        }
        if (!config.hasChange()) {
            return instance;
        }

        Class<?> buildClass = newProxyClass(loader, config);
        Constructor<?> constructor = buildClass.getConstructors()[0];
        return (T) constructor.newInstance(instance);
    }

    /** 创建增强类 */
    public static Class<?> newEnhanceClass(ClassLoader loader, Class<?> superType, Predicate<Method> methodMatcher, MethodInterceptor... interceptors) {
        Objects.requireNonNull(superType, "instance is null.");
        Objects.requireNonNull(methodMatcher, "methodMatcher is null.");
        if (interceptors == null || interceptors.length == 0) {
            return superType;
        }

        DynamicConfig config = new DynamicConfig(realSuperType(superType));
        for (MethodInterceptor interceptor : interceptors) {
            config.addAopInterceptor(methodMatcher, interceptor);
        }

        if (!config.hasChange()) {
            return superType;
        }
        return dynamicClassCache.get(loader, config);
    }

    /** 增强一个类型，使用对象身上的 @Aop 注解 */
    public static Class<?> newEnhanceClass(ClassLoader loader, Class<?> superType) {
        Objects.requireNonNull(superType, "instance is null.");

        DynamicConfig config = new DynamicConfig(realSuperType(superType));
        if (!config.hasChange()) {
            return superType;
        }
        return dynamicClassCache.get(loader, config);
    }

    /** 增强一个对象，使用对象身上的 @Aop 注解 */
    public static Object newEnhanceInstance(ClassLoader loader, Object instance) throws ReflectiveOperationException {
        Objects.requireNonNull(instance, "instance is null.");

        DynamicConfig config = new DynamicConfig(realSuperType(instance.getClass()));
        if (!config.hasChange()) {
            return instance;
        }

        Class<?> buildClass = newProxyClass(loader, config);
        Constructor<?> constructor = buildClass.getConstructors()[0];
        return constructor.newInstance(instance);
    }

    //
    //
    //
    // --------------------------------------------------------------------------------------------------------------------------

    /** 创建一个工厂，创建增强的代理类对象 */
    public static <T> ProxyFactory<T> newProxyFactory(ClassLoader loader, DynamicConfig config) {
        Class<?> buildClass = newProxyClass(loader, config);
        return o -> {
            Constructor<?> constructor = buildClass.getConstructors()[0];
            return (T) constructor.newInstance(o);
        };
    }

    /** 创建增强的代理类，代理类实例相当于对象的代理对象 */
    public static Class<?> newProxyClass(ClassLoader loader, DynamicConfig config) {
        return proxyClassCache.get(loader, config);
    }

    /** 创建增强类 */
    public static Class<?> newEnhanceClass(ClassLoader loader, DynamicConfig config) {
        return dynamicClassCache.get(loader, config);
    }

    private static Class<?> buildAndLoadClass(DynamicConfig config, boolean isProxy, ClassLoader loader) throws ReflectiveOperationException {
        String className = getClassName(config);
        byte[] classBytes = buildClassBytes(config, className, isProxy);
        logClassBytes(className, classBytes, config.getDebugOutputDir());

        // load class
        try {
            Class<?> buildType = (Class<?>) defineClassMethod.invoke(loader, new Object[] { className, classBytes, 0, classBytes.length });
            Field dynamicConfig = buildType.getDeclaredField("dynamicConfig");
            dynamicConfig.setAccessible(true);
            dynamicConfig.set(null, config);
            return buildType;
        } catch (Exception e) {
            throw e;
        }
    }

    private static void logClassBytes(String className, byte[] classBytes, File debugOutputFile) {
        if (debugOutputFile == null) {
            return;
        }

        try {
            String classFullPath = className.replace(".", String.valueOf(File.separatorChar)) + ".class";
            File outFile = new File(debugOutputFile, classFullPath);
            outFile.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(outFile, false)) {
                fos.write(classBytes);
                fos.flush();
            }
        } catch (Exception e) {
            throw ExceptionUtils.toRuntime(e);
        }
    }

    private static String getClassName(DynamicConfig config) {
        String specialNamePrefix = config.getSpecialNamePrefix();
        if (StringUtils.isBlank(specialNamePrefix)) {
            specialNamePrefix = "";
        }
        return specialNamePrefix + "." + config.getSuperClass().getName() + aopClassSuffix + spinIndex.getAndIncrement();
    }

    /** 调用ClassLoader，生成字节码并装载它 */
    private static byte[] buildClassBytes(DynamicConfig config, String newClassName, boolean isProxy) throws ReflectiveOperationException {
        String thisClassName = AsmTools.replaceClassName(newClassName);
        String superClassName = AsmTools.replaceClassName(config.getSuperClass());
        Set<String> interfaces = new LinkedHashSet<>();
        List<String> writerMethod = new ArrayList<>();

        interfaces.add(AsmTools.replaceClassName(DynamicClass.class));
        config.getImplementMap().keySet().forEach(c -> interfaces.add(AsmTools.replaceClassName(c)));
        interfaces.remove(AsmTools.replaceClassName(config.getSuperClass())); // 可能是继承抽象类
        if (isProxy) {
            interfaces.add(AsmTools.replaceClassName(DynamicObject.class));
        }

        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classWriter.visit(V1_6, ACC_PUBLIC + ACC_SUPER, thisClassName, null, superClassName, interfaces.toArray(new String[0]));

        // .dynamicConfig 字段
        FieldVisitor fdc = classWriter.visitField(ACC_PRIVATE + ACC_STATIC, "dynamicConfig", AsmTools.toAsmType(DynamicConfig.class), null, null);
        fdc.visitEnd();

        // .构造方法
        if (isProxy) {
            buildConstructorForProxy(config, classWriter, thisClassName, superClassName);
        } else {
            buildConstructorForBuild(config, classWriter, thisClassName, superClassName);
        }

        //.静态代码块 static
        Map<String, Integer> indexMap = buildStatic(config, classWriter, thisClassName, isProxy);

        // .代理方法
        for (Map.Entry<String, Method> ent : config.getInterceptorMethods().entrySet()) {
            List<String> dynamicMethods = buildDynamicMethod(ent.getKey(), ent.getValue(), indexMap, classWriter, superClassName, thisClassName, isProxy);
            writerMethod.addAll(dynamicMethods);
        }

        // .代理属性
        for (Map.Entry<String, DynamicPropertyInfo> ent : config.getDynamicPropertyMap().entrySet()) {
            List<String> dynamicMethods = buildDynamicProperty(ent.getKey(), ent.getValue(), classWriter, newClassName, thisClassName, isProxy);
            writerMethod.addAll(dynamicMethods);
        }

        // .动态接口
        for (Map.Entry<Class<?>, InvocationHandler> ent : config.getImplementMap().entrySet()) {
            Class<?> faceType = ent.getKey();
            if (faceType == config.getSuperClass()) {
                // 父类是抽象类只有这种情况下才会出现, 那么只处理 抽象方法，相当于实现抽象类
                for (Method implMethod : faceType.getMethods()) {
                    if (Modifier.isAbstract(implMethod.getModifiers())) {
                        String methodName = implMethod.getName();
                        String methodDescriptor = AsmTools.toAsmDesc(implMethod);

                        if (writerMethod.contains(methodName + methodDescriptor)) {
                            continue;
                        }
                        writerMethod.add(methodName + methodDescriptor);
                        buildDynamicImpl(faceType, implMethod, indexMap, classWriter, thisClassName, isProxy);
                    }
                }
            } else if (faceType.isInterface()) {
                // 常规下动态接口,全部是 接口
                for (Method implMethod : faceType.getMethods()) {
                    String methodName = implMethod.getName();
                    String methodDescriptor = AsmTools.toAsmDesc(implMethod);

                    if (writerMethod.contains(methodName + methodDescriptor)) {
                        continue;
                    }
                    writerMethod.add(methodName + methodDescriptor);
                    buildDynamicImpl(faceType, implMethod, indexMap, classWriter, thisClassName, isProxy);
                }
            } else {
                throw new RuntimeException("Internal error.");
            }
        }

        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private static Map<String, Integer> buildStatic(DynamicConfig config, ClassWriter classWriter, String thisClassName, boolean isProxy) throws NoSuchMethodException {
        Map<String, Integer> indexMap = new HashMap<>();
        List<Class<?>> implClassList = new ArrayList<>(config.getImplementMap().keySet());
        long arraySize = (config.getInterceptorMap().size() + implClassList.stream().flatMap((Function<Class<?>, Stream<Method>>) t -> Arrays.stream(t.getMethods())).count());
        if (arraySize > Integer.MAX_VALUE) {
            throw new ArrayIndexOutOfBoundsException("there are too many ways to method.");
        }

        if (!isProxy) {
            FieldVisitor fv1 = classWriter.visitField(ACC_PRIVATE + ACC_STATIC, "proxyMethod", AsmTools.toAsmType(Method[].class), null, null);
            fv1.visitEnd();
        }
        FieldVisitor fv2 = classWriter.visitField(ACC_PRIVATE + ACC_STATIC, "targetMethod", AsmTools.toAsmType(Method[].class), null, null);
        fv2.visitEnd();

        int superClassIndex = 0;// 0 位置是 superClass
        int thisClassIndex = 1;// 0 位置是 thisClass
        int implsClassIndex = 2;// 0 位置是 implsClass

        MethodVisitor mv = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();
        Label tryStartLabel = new Label();
        Label tryStartCodeLabel = new Label();
        Label tryCacheLabel = new Label();
        Label tryEndLabel = new Label();
        Label returnLabel = new Label();
        Label cacheStartCodeLabel = new Label();

        mv.visitTryCatchBlock(tryStartLabel, tryCacheLabel, tryEndLabel, AsmTools.replaceClassName(Throwable.class));
        mv.visitLabel(tryStartLabel);
        mv.visitLdcInsn(Type.getType(AsmTools.toAsmType(config.getSuperClass())));
        mv.visitVarInsn(ASTORE, superClassIndex);
        mv.visitLdcInsn(Type.getType("L" + thisClassName + ";"));
        mv.visitVarInsn(ASTORE, thisClassIndex);

        mv.visitIntInsn(BIPUSH, implClassList.size());
        mv.visitTypeInsn(ANEWARRAY, AsmTools.replaceClassName(Class.class));
        for (int i = 0; i < implClassList.size(); i++) {
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, i);
            mv.visitLdcInsn(Type.getType("L" + AsmTools.replaceClassName(implClassList.get(i)) + ";"));
            mv.visitInsn(AASTORE);
        }
        mv.visitVarInsn(ASTORE, implsClassIndex);

        mv.visitLabel(tryStartCodeLabel);
        mv.visitIntInsn(BIPUSH, (int) arraySize);
        mv.visitTypeInsn(ANEWARRAY, AsmTools.replaceClassName(Method.class));
        mv.visitFieldInsn(PUTSTATIC, thisClassName, "targetMethod", AsmTools.toAsmType(Method[].class));
        if (!isProxy) {
            mv.visitIntInsn(BIPUSH, (int) arraySize);
            mv.visitTypeInsn(ANEWARRAY, AsmTools.replaceClassName(Method.class));
            mv.visitFieldInsn(PUTSTATIC, thisClassName, "proxyMethod", AsmTools.toAsmType(Method[].class));
        }

        int i = -1;
        String getMethodDesc = AsmTools.toAsmDesc(Class.class.getMethod("getMethod", String.class, Class[].class));
        for (Map.Entry<String, Method> ent : config.getInterceptorMethods().entrySet()) {
            Method aopMethod = ent.getValue();
            String asmMethodName = aopMethod.getName();
            String[] parameterTypes = AsmTools.splitAsmType(AsmTools.toAsmType(aopMethod.getParameterTypes()));
            indexMap.put(AsmTools.toAsmFullDesc(aopMethod), ++i);
            //
            // targetMethod[n] = superClass.getMethod("xxxx",new Class[] { xxx,xxx});
            mv.visitFieldInsn(GETSTATIC, thisClassName, "targetMethod", AsmTools.toAsmType(Method[].class));
            mv.visitIntInsn(BIPUSH, i);
            mv.visitVarInsn(ALOAD, superClassIndex); // superClass
            mv.visitLdcInsn(asmMethodName);
            AsmTools.codeBuilder_2(mv, parameterTypes);
            mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(Class.class), "getMethod", getMethodDesc, false);
            mv.visitInsn(AASTORE);
            //
            // targetMethod[n].setAccessible(true);
            mv.visitFieldInsn(GETSTATIC, thisClassName, "targetMethod", AsmTools.toAsmType(Method[].class));
            mv.visitIntInsn(BIPUSH, i);
            mv.visitInsn(AALOAD);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(Method.class), "setAccessible", "(Z)V", false);
            //
            if (!isProxy) {
                // propxyMethod[n] = superClass.getMethod("aop$" + "xxxx",new Class[] { xxx,xxx});
                mv.visitFieldInsn(GETSTATIC, thisClassName, "proxyMethod", AsmTools.toAsmType(Method[].class));
                mv.visitIntInsn(BIPUSH, i);
                mv.visitVarInsn(ALOAD, thisClassIndex); // thisClass
                mv.visitLdcInsn(aopMethodSuffix + asmMethodName);
                AsmTools.codeBuilder_2(mv, parameterTypes);
                mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(Class.class), "getDeclaredMethod", getMethodDesc, false);
                mv.visitInsn(AASTORE);
                //
                // targetMethod[n].setAccessible(true);
                mv.visitFieldInsn(GETSTATIC, thisClassName, "proxyMethod", AsmTools.toAsmType(Method[].class));
                mv.visitIntInsn(BIPUSH, i);
                mv.visitInsn(AALOAD);
                mv.visitInsn(ICONST_1);
                mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(Method.class), "setAccessible", "(Z)V", false);
            }
        }

        for (Class<?> ent : implClassList) {
            String classType = AsmTools.toAsmType(ent);
            for (Method entMethod : ent.getMethods()) {
                String asmMethodName = entMethod.getName();
                String[] parameterTypes = AsmTools.splitAsmType(AsmTools.toAsmType(entMethod.getParameterTypes()));
                indexMap.put(classType + "." + AsmTools.toAsmFullDesc(entMethod), ++i);
                int implIndex = implClassList.indexOf(ent);

                // targetMethod[n] = superClass.getMethod("xxxx",new Class[] { xxx,xxx});
                mv.visitFieldInsn(GETSTATIC, thisClassName, "targetMethod", AsmTools.toAsmType(Method[].class));
                mv.visitIntInsn(BIPUSH, i);
                mv.visitVarInsn(ALOAD, thisClassIndex); // thisClass
                mv.visitLdcInsn(asmMethodName);
                AsmTools.codeBuilder_2(mv, parameterTypes);
                mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(Class.class), "getMethod", getMethodDesc, false);
                mv.visitInsn(AASTORE);

                // proxyMethod[n] = superClass.getMethod("xxxx",new Class[] { xxx,xxx});
                mv.visitFieldInsn(GETSTATIC, thisClassName, "proxyMethod", AsmTools.toAsmType(Method[].class));
                mv.visitIntInsn(BIPUSH, i);
                mv.visitVarInsn(ALOAD, implsClassIndex); // implsClass
                mv.visitIntInsn(BIPUSH, implIndex);
                mv.visitInsn(AALOAD);
                mv.visitLdcInsn(asmMethodName);
                AsmTools.codeBuilder_2(mv, parameterTypes);
                mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(Class.class), "getMethod", getMethodDesc, false);
                mv.visitInsn(AASTORE);
            }
        }

        mv.visitLabel(tryCacheLabel);
        mv.visitJumpInsn(GOTO, returnLabel);
        mv.visitLabel(tryEndLabel);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { AsmTools.replaceClassName(Throwable.class) });
        mv.visitVarInsn(ASTORE, 0);
        mv.visitLabel(cacheStartCodeLabel);
        mv.visitVarInsn(ALOAD, 0);
        String exceptionMethodDesc = AsmTools.toAsmDesc(ExceptionUtils.class.getMethod("toRuntime", Throwable.class));
        mv.visitMethodInsn(INVOKESTATIC, AsmTools.replaceClassName(ExceptionUtils.class), "toRuntime", exceptionMethodDesc, false);
        mv.visitInsn(ATHROW);
        mv.visitLabel(returnLabel);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(RETURN);
        mv.visitLocalVariable("superClass", AsmTools.toAsmType(Class.class), "Ljava/lang/Class<*>;", tryStartLabel, tryCacheLabel, 0);
        mv.visitLocalVariable("thisClass", AsmTools.toAsmType(Class.class), "Ljava/lang/Class<*>;", tryStartLabel, tryCacheLabel, 1);
        mv.visitLocalVariable("implClass", "[Ljava/lang/Class;", "[Ljava/lang/Class<*>;", tryStartLabel, tryCacheLabel, 2);
        //mv.visitLocalVariable("e", AsmTools.toAsmType(Throwable.class), null, cacheStartCodeLabel, returnLabel, 0);
        mv.visitMaxs(8, 1);
        mv.visitEnd();

        return indexMap;
    }

    private static void buildConstructorForBuild(DynamicConfig config, ClassWriter classWriter, String thisClassName, String superClassName) {
        Constructor<?>[] constructorArray = config.getSuperClass().getConstructors();
        if (constructorArray.length == 0) {
            MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(4, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "()V", false);
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "L" + thisClassName + ";", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        for (Constructor<?> constructor : constructorArray) {
            String[] asmParams = AsmTools.splitAsmType(AsmTools.toAsmType(constructor.getParameterTypes()));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
            String[] throwStrArray = AsmTools.replaceClassName(constructor.getExceptionTypes());
            String paramsStr = "(" + AsmTools.toAsmType(constructor.getParameterTypes()) + ")V";
            //
            AtomicInteger variableIndexCounters = new AtomicInteger(0);
            Map<String, Integer> paramIndexMap = new LinkedHashMap<>();
            paramIndexMap.put("this", 0);
            for (int i = 0; i < asmParams.length; i++) {
                paramIndexMap.put("args" + i, variableIndexCounters.incrementAndGet());
                if ("D".equals(asmParams[i])) {
                    variableIndexCounters.incrementAndGet();// double 需要额外增加1
                }
                if ("J".equals(asmParams[i])) {
                    variableIndexCounters.incrementAndGet();// long 需要额外增加1
                }
            }
            //
            Label startBlock = new Label();
            Label endBlock = new Label();
            //
            MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", paramsStr, null, throwStrArray);
            mv.visitCode();
            mv.visitLabel(startBlock);
            mv.visitVarInsn(ALOAD, paramIndexMap.get("this"));
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                mv.visitVarInsn(AsmTools.getLoad(AsmTools.toAsmType(parameterTypes[i])), paramIndexMap.get("args" + i));
            }
            mv.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", paramsStr, false);
            mv.visitInsn(RETURN);
            mv.visitLabel(endBlock);
            mv.visitLocalVariable("this", "L" + thisClassName + ";", null, startBlock, endBlock, paramIndexMap.get("this"));
            for (int i = 0; i < parameterTypes.length; i++) {
                mv.visitLocalVariable("args" + i, AsmTools.toAsmType(parameterTypes[i]), null, startBlock, endBlock, paramIndexMap.get("args" + i));
            }
            int maxStack = parameterTypes.length + 1;
            mv.visitMaxs(maxStack, maxStack);
            mv.visitEnd();
        }
    }

    private static void buildConstructorForProxy(DynamicConfig config, ClassWriter classWriter, String thisClassName, String superClassName) {
        String superClassRef = "L" + superClassName + ";";
        String thisClassRef = "L" + thisClassName + ";";

        // field.
        FieldVisitor fv1 = classWriter.visitField(ACC_PRIVATE, "proxy", superClassRef, null, null);
        fv1.visitEnd();

        // constructor.
        {
            Label startBlock = new Label();
            Label endBlock = new Label();
            MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(" + superClassRef + ")V", null, null);
            mv.visitCode();
            mv.visitCode();
            mv.visitLabel(startBlock);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, thisClassName, "proxy", superClassRef);
            mv.visitInsn(RETURN);
            mv.visitLabel(endBlock);
            mv.visitLocalVariable("this", thisClassRef, null, startBlock, endBlock, 0);
            mv.visitLocalVariable("proxy", superClassRef, null, startBlock, endBlock, 1);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }

        // getOriginalObject
        {
            Label startBlock = new Label();
            Label endBlock = new Label();
            MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "getOriginalObject", "()Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, thisClassName, "proxy", superClassRef);
            mv.visitInsn(ARETURN);
            mv.visitLocalVariable("this", thisClassRef, null, startBlock, endBlock, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
    }

    private static List<String> buildDynamicMethod(String aopMethodKey, Method aopMethod, Map<String, Integer> indexMap, ClassWriter classWriter, String superClassName, String thisClassName, boolean isProxy) throws NoSuchMethodException {
        String[] asmParams = AsmTools.splitAsmType(AsmTools.toAsmType(aopMethod.getParameterTypes()));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String[] throwStrArray = AsmTools.replaceClassName(aopMethod.getExceptionTypes());
        List<String> outputMethod = new ArrayList<>();

        AtomicInteger variableIndexCounters = new AtomicInteger(0);
        Map<String, Integer> paramIndexMap = new LinkedHashMap<>();
        paramIndexMap.put("this", 0);
        for (int i = 0; i < asmParams.length; i++) {
            paramIndexMap.put("args" + i, variableIndexCounters.incrementAndGet());
            if ("D".equals(asmParams[i])) {
                variableIndexCounters.incrementAndGet();// double 需要额外增加1
            }
            if ("J".equals(asmParams[i])) {
                variableIndexCounters.incrementAndGet();// long 需要额外增加1
            }
        }

        if (!isProxy) {
            String methodName = aopMethodSuffix + aopMethod.getName();
            String methodDesc = AsmTools.toAsmDesc(aopMethod);
            outputMethod.add(methodName + methodDesc);
            MethodVisitor replacementVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_FINAL, methodName, methodDesc, AsmTools.toAsmSignature(aopMethod), throwStrArray);
            replacementVisitor.visitCode();
            replacementVisitor.visitVarInsn(ALOAD, 0);
            for (int i = 0; i < asmParams.length; i++) {
                replacementVisitor.visitVarInsn(AsmTools.getLoad(asmParams[i]), paramIndexMap.get("args" + i));
            }
            replacementVisitor.visitMethodInsn(INVOKESPECIAL, superClassName, aopMethod.getName(), AsmTools.toAsmDesc(aopMethod), false);
            replacementVisitor.visitInsn(AsmTools.getReturn(AsmTools.toAsmType(aopMethod.getReturnType())));
            replacementVisitor.visitMaxs(-1, -1);
            replacementVisitor.visitEnd();
        }

        int paramsIndexMark = variableIndexCounters.get();
        paramIndexMap.put("paramObjects", variableIndexCounters.incrementAndGet());
        paramIndexMap.put("returnData", variableIndexCounters.incrementAndGet());
        paramIndexMap.put("e", paramsIndexMark + 1);// is in cache so recount
        //
        Label tryStartLabel = new Label();
        Label tryCacheLabel = new Label();
        Label tryEndLabel = new Label();
        Label paramObjectsLabel = new Label();
        Label returnDataLabel = new Label();
        Label returnLabel = new Label();
        Label eLabel = new Label();
        //
        String methodName = aopMethod.getName();
        String methodDesc = AsmTools.toAsmDesc(aopMethod);
        outputMethod.add(methodName + methodDesc);
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, methodName, methodDesc, AsmTools.toAsmSignature(aopMethod), throwStrArray);
        mv.visitCode();
        mv.visitTryCatchBlock(tryStartLabel, tryCacheLabel, tryEndLabel, AsmTools.replaceClassName(Throwable.class));
        mv.visitLabel(tryStartLabel);
        //
        // Object[] paramObjects = new Object[] { longValue, doubleValue };
        if (asmParams.length == 0) {
            mv.visitInsn(ACONST_NULL);
        } else {
            AsmTools.codeBuilder_1(mv, asmParams, paramIndexMap);
        }
        mv.visitVarInsn(ASTORE, paramIndexMap.get("paramObjects"));
        mv.visitLabel(paramObjectsLabel);
        //
        // Object obj = new InnerAopInvocation("checkBaseType1", targetMethod[0], this, pObjects).proceed();
        mv.visitTypeInsn(NEW, AsmTools.replaceClassName(InvokerMethodInvocation.class));
        mv.visitInsn(DUP);
        mv.visitInsn(isProxy ? ICONST_1 : ICONST_0);
        mv.visitLdcInsn(aopMethodKey);
        mv.visitFieldInsn(GETSTATIC, thisClassName, "targetMethod", AsmTools.toAsmType(Method[].class));
        mv.visitIntInsn(BIPUSH, indexMap.get(AsmTools.toAsmFullDesc(aopMethod)));
        mv.visitInsn(AALOAD);
        if (!isProxy) {
            mv.visitFieldInsn(GETSTATIC, thisClassName, "proxyMethod", AsmTools.toAsmType(Method[].class));
            mv.visitIntInsn(BIPUSH, indexMap.get(AsmTools.toAsmFullDesc(aopMethod)));
            mv.visitInsn(AALOAD);
        } else {
            mv.visitInsn(ACONST_NULL);
        }
        mv.visitFieldInsn(GETSTATIC, thisClassName, "dynamicConfig", AsmTools.toAsmType(DynamicConfig.class));
        mv.visitVarInsn(ALOAD, paramIndexMap.get("this"));
        mv.visitVarInsn(ALOAD, paramIndexMap.get("paramObjects"));
        String initDesc = AsmTools.toAsmType(InvokerMethodInvocation.class.getConstructor(boolean.class, String.class, Method.class, Method.class, DynamicConfig.class, Object.class, Object[].class).getParameterTypes());
        mv.visitMethodInsn(INVOKESPECIAL, AsmTools.replaceClassName(InvokerMethodInvocation.class), "<init>", "(" + initDesc + ")V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(InvokerMethodInvocation.class), "proceed", "()Ljava/lang/Object;", false);
        mv.visitVarInsn(ASTORE, paramIndexMap.get("returnData"));
        mv.visitLabel(returnDataLabel);
        // return (List) obj;
        mv.visitVarInsn(ALOAD, paramIndexMap.get("returnData"));
        AsmTools.codeBuilder_3(mv, AsmTools.toAsmType(aopMethod.getReturnType()), tryCacheLabel);
        //
        mv.visitLabel(tryEndLabel);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/Throwable" });
        //
        //            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        //            int finalElseFrameMode = 0;
        //            Class<?>[] exceptionTypes = aopMethod.getExceptionTypes();
        //            for (int i = 0; i < exceptionTypes.length; i++) {
        //                if (i == 0) {
        //                    finalElseFrameMode = Opcodes.F_APPEND;
        //                } else if (i == 1) {
        //                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/lang/Throwable" }, 0, null);
        //                    finalElseFrameMode = Opcodes.F_SAME;
        //                } else {
        //                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        //                    finalElseFrameMode = Opcodes.F_SAME;
        //                }
        //                //
        //                Label ifEnd = new Label();
        //                mv.visitVarInsn(ALOAD, paramIndexMap.get("e"));
        //                mv.visitTypeInsn(INSTANCEOF, AsmTools.replaceClassName(exceptionTypes[i]));
        //                mv.visitJumpInsn(IFEQ, ifEnd);
        //                mv.visitVarInsn(ALOAD, paramIndexMap.get("e"));
        //                mv.visitTypeInsn(CHECKCAST, AsmTools.replaceClassName(exceptionTypes[i]));
        //                mv.visitInsn(ATHROW);
        //                mv.visitLabel(ifEnd);
        //            }
        //            //
        //            if (finalElseFrameMode == Opcodes.F_APPEND) {
        //                mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/lang/Throwable" }, 0, null);
        //            } else {
        //                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        //            }
        //
        mv.visitVarInsn(ASTORE, paramIndexMap.get("e"));
        mv.visitLabel(eLabel);
        mv.visitVarInsn(ALOAD, paramIndexMap.get("e"));
        String exceptionMethodDesc = AsmTools.toAsmDesc(ExceptionUtils.class.getMethod("toRuntime", Throwable.class));
        mv.visitMethodInsn(INVOKESTATIC, AsmTools.replaceClassName(ExceptionUtils.class), "toRuntime", exceptionMethodDesc, false);
        mv.visitInsn(ATHROW);
        //
        mv.visitLabel(returnLabel);
        mv.visitLocalVariable("paramObjects", "[Ljava/lang/Object;", null, paramObjectsLabel, tryEndLabel, paramIndexMap.get("paramObjects"));
        mv.visitLocalVariable("returnData", "Ljava/lang/Object;", null, returnDataLabel, tryEndLabel, paramIndexMap.get("returnData"));
        mv.visitLocalVariable("e", "Ljava/lang/Throwable;", null, eLabel, returnLabel, paramIndexMap.get("e"));
        mv.visitLocalVariable("this", "L" + thisClassName + ";", null, tryStartLabel, returnLabel, paramIndexMap.get("this"));
        for (int i = 0; i < asmParams.length; i++) {
            mv.visitLocalVariable("args" + i, asmParams[i], null, tryStartLabel, returnLabel, paramIndexMap.get("args" + i));
        }
        //
        mv.visitMaxs(-1, -1);
        mv.visitEnd();

        return outputMethod;
    }

    private static List<String> buildDynamicProperty(String propertyName, DynamicPropertyInfo propertyInfo, ClassWriter classWriter, String newClassName, String thisClassName, boolean isProxy) throws NoSuchMethodException {
        List<String> outputMethod = new ArrayList<>();
        String asmType = AsmTools.toAsmType(propertyInfo.propertyType);
        {
            Label sLabel = new Label();
            Label eLabel = new Label();
            String methodName = ((propertyInfo.propertyType == Boolean.TYPE) ? "is" : "get") + StringUtils.firstCharToUpperCase(propertyName);
            String methodDescriptor = "()" + asmType;
            Method propertyMethod = InvokerDynamicProperty.class.getMethod("getProperty", boolean.class, DynamicConfig.class, String.class, Object.class, String.class);
            //
            outputMethod.add(methodName + methodDescriptor);
            MethodVisitor mv = classWriter.visitMethod(Opcodes.ACC_PUBLIC, methodName, methodDescriptor, null, new String[] { "java/lang/Throwable" });
            mv.visitLabel(sLabel);
            mv.visitInsn(isProxy ? ICONST_1 : ICONST_0);
            mv.visitFieldInsn(GETSTATIC, thisClassName, "dynamicConfig", AsmTools.toAsmType(DynamicConfig.class));
            mv.visitLdcInsn(newClassName + "_get_" + propertyName);
            mv.visitVarInsn(ALOAD, 0);// this
            mv.visitLdcInsn(propertyName);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, AsmTools.replaceClassName(InvokerDynamicProperty.class), propertyMethod.getName(), AsmTools.toAsmDesc(propertyMethod), false);
            AsmTools.codeBuilder_Cast(mv, asmType, null);
            mv.visitInsn(AsmTools.getReturn(asmType));
            mv.visitLabel(eLabel);
            mv.visitLocalVariable("this", AsmTools.toAsmType(InvokerDynamicProperty.class), null, sLabel, eLabel, 0);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
        }
        if (propertyInfo.rwType == ReadWriteType.ReadWrite) {
            Label sLabel = new Label();
            Label eLabel = new Label();
            String methodName = "set" + StringUtils.firstCharToUpperCase(propertyName);
            String methodDescriptor = "(" + asmType + ")V";
            Method propertyMethod = InvokerDynamicProperty.class.getMethod("setProperty", boolean.class, DynamicConfig.class, String.class, Object.class, String.class, Object.class);
            //
            outputMethod.add(methodName + methodDescriptor);
            MethodVisitor mv = classWriter.visitMethod(Opcodes.ACC_PUBLIC, methodName, methodDescriptor, null, new String[] { "java/lang/Throwable" });
            mv.visitCode();
            mv.visitLabel(sLabel);
            mv.visitInsn(isProxy ? ICONST_1 : ICONST_0);
            mv.visitFieldInsn(GETSTATIC, thisClassName, "dynamicConfig", AsmTools.toAsmType(DynamicConfig.class));
            mv.visitLdcInsn(newClassName + "_set_" + propertyName);
            mv.visitVarInsn(ALOAD, 0);// this
            mv.visitLdcInsn(propertyName);
            mv.visitVarInsn(AsmTools.getLoad(asmType), 1);
            AsmTools.codeBuilder_valueOf(mv, AsmTools.toAsmType(propertyInfo.propertyType));
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, AsmTools.replaceClassName(InvokerDynamicProperty.class), propertyMethod.getName(), AsmTools.toAsmDesc(propertyMethod), false);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitLabel(eLabel);
            mv.visitLocalVariable("this", AsmTools.toAsmType(InvokerDynamicProperty.class), null, sLabel, eLabel, 0);
            mv.visitLocalVariable(propertyName, AsmTools.toAsmType(Object.class), null, sLabel, eLabel, 1);
            mv.visitMaxs(4, 2);
            mv.visitEnd();
        }
        return outputMethod;
    }

    private static void buildDynamicImpl(Class<?> implType, Method implMethod, Map<String, Integer> indexMap, ClassWriter classWriter, String thisClassName, boolean isProxy) throws NoSuchMethodException {
        String methodKey = AsmTools.toAsmType(implType) + "." + AsmTools.toAsmFullDesc(implMethod);
        String[] asmParams = AsmTools.splitAsmType(AsmTools.toAsmType(implMethod.getParameterTypes()));//"IIIILjava/lang/Integer;F[[[ILjava/lang.Boolean;"
        String[] throwStrArray = AsmTools.replaceClassName(implMethod.getExceptionTypes());

        AtomicInteger variableIndexCounters = new AtomicInteger(0);
        Map<String, Integer> paramIndexMap = new LinkedHashMap<>();
        paramIndexMap.put("this", 0);
        for (int i = 0; i < asmParams.length; i++) {
            paramIndexMap.put("args" + i, variableIndexCounters.incrementAndGet());
            if ("D".equals(asmParams[i])) {
                variableIndexCounters.incrementAndGet();// double 需要额外增加1
            }
            if ("J".equals(asmParams[i])) {
                variableIndexCounters.incrementAndGet();// long 需要额外增加1
            }
        }

        int paramsIndexMark = variableIndexCounters.get();
        paramIndexMap.put("paramObjects", variableIndexCounters.incrementAndGet());
        paramIndexMap.put("returnData", variableIndexCounters.incrementAndGet());
        paramIndexMap.put("e", paramsIndexMark + 1);// is in cache so recount

        Label tryStartLabel = new Label();
        Label tryCacheLabel = new Label();
        Label tryEndLabel = new Label();
        Label paramObjectsLabel = new Label();
        Label returnDataLabel = new Label();
        Label returnLabel = new Label();
        Label eLabel = new Label();

        String methodName = implMethod.getName();
        String methodDesc = AsmTools.toAsmDesc(implMethod);
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, methodName, methodDesc, AsmTools.toAsmSignature(implMethod), throwStrArray);
        mv.visitCode();
        mv.visitTryCatchBlock(tryStartLabel, tryCacheLabel, tryEndLabel, AsmTools.replaceClassName(Throwable.class));
        mv.visitLabel(tryStartLabel);

        // Object[] paramObjects = new Object[] { longValue, doubleValue };
        if (asmParams.length == 0) {
            mv.visitInsn(ACONST_NULL);
        } else {
            AsmTools.codeBuilder_1(mv, asmParams, paramIndexMap);
        }
        mv.visitVarInsn(ASTORE, paramIndexMap.get("paramObjects"));
        mv.visitLabel(paramObjectsLabel);

        // Object obj = new InvokerImplementInvocation("checkBaseType1", targetMethod[0], this, pObjects).proceed();
        mv.visitTypeInsn(NEW, AsmTools.replaceClassName(InvokerImplementInvocation.class));
        mv.visitInsn(DUP);
        mv.visitInsn(isProxy ? ICONST_1 : ICONST_0);
        mv.visitLdcInsn(Type.getType("L" + AsmTools.toAsmType(implType) + ";"));
        mv.visitFieldInsn(GETSTATIC, thisClassName, "targetMethod", AsmTools.toAsmType(Method[].class));
        mv.visitIntInsn(BIPUSH, indexMap.get(methodKey));
        mv.visitInsn(AALOAD);
        mv.visitFieldInsn(GETSTATIC, thisClassName, "proxyMethod", AsmTools.toAsmType(Method[].class));
        mv.visitIntInsn(BIPUSH, indexMap.get(methodKey));
        mv.visitInsn(AALOAD);
        mv.visitFieldInsn(GETSTATIC, thisClassName, "dynamicConfig", AsmTools.toAsmType(DynamicConfig.class));
        mv.visitVarInsn(ALOAD, paramIndexMap.get("this"));
        mv.visitVarInsn(ALOAD, paramIndexMap.get("paramObjects"));
        String initDesc = AsmTools.toAsmType(InvokerImplementInvocation.class.getConstructor(boolean.class, Class.class, Method.class, Method.class, DynamicConfig.class, Object.class, Object[].class).getParameterTypes());
        mv.visitMethodInsn(INVOKESPECIAL, AsmTools.replaceClassName(InvokerImplementInvocation.class), "<init>", "(" + initDesc + ")V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, AsmTools.replaceClassName(InvokerImplementInvocation.class), "proceed", "()Ljava/lang/Object;", false);
        mv.visitVarInsn(ASTORE, paramIndexMap.get("returnData"));
        mv.visitLabel(returnDataLabel);

        // return (List) obj;
        mv.visitVarInsn(ALOAD, paramIndexMap.get("returnData"));
        AsmTools.codeBuilder_3(mv, AsmTools.toAsmType(implMethod.getReturnType()), tryCacheLabel);

        mv.visitLabel(tryEndLabel);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[] { "java/lang/Throwable" });

        //            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        //            int finalElseFrameMode = 0;
        //            Class<?>[] exceptionTypes = aopMethod.getExceptionTypes();
        //            for (int i = 0; i < exceptionTypes.length; i++) {
        //                if (i == 0) {
        //                    finalElseFrameMode = Opcodes.F_APPEND;
        //                } else if (i == 1) {
        //                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/lang/Throwable" }, 0, null);
        //                    finalElseFrameMode = Opcodes.F_SAME;
        //                } else {
        //                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        //                    finalElseFrameMode = Opcodes.F_SAME;
        //                }
        //
        //                Label ifEnd = new Label();
        //                mv.visitVarInsn(ALOAD, paramIndexMap.get("e"));
        //                mv.visitTypeInsn(INSTANCEOF, AsmTools.replaceClassName(exceptionTypes[i]));
        //                mv.visitJumpInsn(IFEQ, ifEnd);
        //                mv.visitVarInsn(ALOAD, paramIndexMap.get("e"));
        //                mv.visitTypeInsn(CHECKCAST, AsmTools.replaceClassName(exceptionTypes[i]));
        //                mv.visitInsn(ATHROW);
        //                mv.visitLabel(ifEnd);
        //            }
        //
        //            if (finalElseFrameMode == Opcodes.F_APPEND) {
        //                mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "java/lang/Throwable" }, 0, null);
        //            } else {
        //                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        //            }

        mv.visitVarInsn(ASTORE, paramIndexMap.get("e"));
        mv.visitLabel(eLabel);
        mv.visitVarInsn(ALOAD, paramIndexMap.get("e"));
        String exceptionMethodDesc = AsmTools.toAsmDesc(ExceptionUtils.class.getMethod("toRuntime", Throwable.class));
        mv.visitMethodInsn(INVOKESTATIC, AsmTools.replaceClassName(ExceptionUtils.class), "toRuntime", exceptionMethodDesc, false);
        mv.visitInsn(ATHROW);

        mv.visitLabel(returnLabel);
        mv.visitLocalVariable("paramObjects", "[Ljava/lang/Object;", null, paramObjectsLabel, tryEndLabel, paramIndexMap.get("paramObjects"));
        mv.visitLocalVariable("returnData", "Ljava/lang/Object;", null, returnDataLabel, tryEndLabel, paramIndexMap.get("returnData"));
        mv.visitLocalVariable("e", "Ljava/lang/Throwable;", null, eLabel, returnLabel, paramIndexMap.get("e"));
        mv.visitLocalVariable("this", "L" + thisClassName + ";", null, tryStartLabel, returnLabel, paramIndexMap.get("this"));
        for (int i = 0; i < asmParams.length; i++) {
            mv.visitLocalVariable("args" + i, asmParams[i], null, tryStartLabel, returnLabel, paramIndexMap.get("args" + i));
        }

        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    public static Class<?> getPrototypeType(Object instance) {
        if (instance == null) {
            return null;
        } else {
            Class<?> instanceClass = instance.getClass();
            return isDynamicObject(instance) ? instanceClass.getSuperclass() : instanceClass;
        }
    }

    public static Class<?> getPrototypeType(Class<?> targetType) {
        if (targetType == null) {
            return null;
        } else {
            return isDynamicClass(targetType) ? targetType.getSuperclass() : targetType;
        }
    }

    public static boolean isProxyObject(Object instance) {
        return instance instanceof DynamicObject;
    }

    public static boolean isDynamicObject(Object instance) {
        return instance instanceof DynamicClass;
    }

    public static boolean isProxyClass(Class<?> targetType) {
        return targetType != null && DynamicObject.class.isAssignableFrom(targetType);
    }

    public static boolean isDynamicClass(Class<?> targetType) {
        return targetType != null && DynamicClass.class.isAssignableFrom(targetType);
    }
}
