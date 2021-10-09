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
package net.hasor.cobble.loader;
import net.hasor.cobble.ExceptionUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @version : 2014年9月7日
 * @author 赵永春 (zyc@hasor.net)
 */
class LoaderClassScanner implements ClassFinder {
    private final ResourceLoader         resourceLoader;
    private final ClassLoader            classLoader;
    private final Map<String, ClassInfo> classInfoMap = new ConcurrentHashMap<>();

    public LoaderClassScanner(ResourceLoader resourceLoader, ClassLoader classLoader) {
        this.resourceLoader = resourceLoader;
        this.classLoader = classLoader;
    }

    @Override
    public Set<Class<?>> getClassSet(String[] loadPackages, ClassMatcher matcher) {
        try {
            String[] packages = new String[loadPackages.length];
            for (int i = 0; i < loadPackages.length; i++) {
                packages[i] = loadPackages[i].replace(".", "/");
            }
            List<Class<?>> classes = this.resourceLoader.scanResources(ResourceLoader.MatchType.Prefix, event -> {
                String className = event.getName();
                if (!className.endsWith(".class")) {
                    return null;
                } else {
                    className = className.substring(0, className.length() - 6);
                    className = className.replace("/", ".");
                    try {
                        return testAndLoad(className, matcher);
                    } catch (ClassNotFoundException e) {
                        throw new IOException(e);
                    }
                }
            }, packages);
            return new HashSet<>(classes);
        } catch (IOException e) {
            throw new IllegalStateException(ExceptionUtils.getRootCause(e));
        }
    }

    protected Class<?> testAndLoad(String className, ClassMatcher matcher) throws IOException, ClassNotFoundException {
        ClassInfo classInfo = null;
        if (this.classInfoMap.containsKey(className)) {
            classInfo = this.classInfoMap.get(className);
        }
        if (classInfo == null) {
            classInfo = loadClassInfo(className);
            if (classInfo == null) {
                return null;
            }
            this.classInfoMap.put(className, classInfo);
        }
        //
        ClassMatcherContext context = new ClassMatcherContext(classInfo) {
            @Override
            public ClassInfo loadClass(String className) throws IOException {
                return loadClassInfo(className);
            }
        };
        if (matcher.matcher(context)) {
            return classLoader.loadClass(className);
        } else {
            return null;
        }
    }

    /**分析类的字节码，分析过程中会递归解析父类和实现的接口*/
    @Override
    public ClassInfo loadClassInfo(String className) throws IOException {
        try (InputStream classStream = this.resourceLoader.getResourceAsStream(className.replace('.', '/') + ".class")) {
            if (classStream == null) {
                return null;
            } else {
                return loadClassInfo(className, classStream);
            }
        }
    }

    /** 分析类的字节码，分析过程中会递归解析父类和实现的接口 */
    private ClassInfo loadClassInfo(String className, final InputStream inStream) throws IOException {
        /*一、检查类是否已经被加载过，避免重复扫描同一个类*/
        if (this.classInfoMap.containsKey(className)) {
            return this.classInfoMap.get(className);
        }
        /*二、使用 ClassReader 读取类的基本信息*/
        ClassReader classReader = new ClassReader(inStream);
        //className = classReader.getClassName().replace('/', '.');
        /*三、读取类的（名称、父类、接口、注解）信息*/
        final ClassInfo info = new ClassInfo();
        classReader.accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
                //1.读取基本信息
                info.className = name.replace('/', '.');
                if (superName != null) {
                    info.superName = superName.replace('/', '.');
                }
                //2.读取接口
                info.interFaces = interfaces;
                for (int i = 0; i < info.interFaces.length; i++) {
                    info.interFaces[i] = info.interFaces[i].replace('/', '.');
                }
                super.visit(version, access, name, signature, superName, interfaces);
            }

            @Override
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                //3.扫描类信息，获取标记的注解
                /* 将一个Ljava/lang/Object;形式的字符串转化为java/lang/Object形式。*/
                String[] annoArrays = info.annos == null ? new String[0] : info.annos;
                //
                String[] newAnnoArrays = new String[annoArrays.length + 1];
                System.arraycopy(annoArrays, 0, newAnnoArrays, 0, annoArrays.length);
                //
                String annnoType = desc.substring(1, desc.length() - 1);
                newAnnoArrays[newAnnoArrays.length - 1] = annnoType.replace('/', '.');
                //
                info.annos = newAnnoArrays;
                return super.visitAnnotation(desc, visible);
            }
        }, ClassReader.SKIP_CODE);
        //四、递归解析父类
        if (info.superName != null) {
            try (InputStream superStream = this.resourceLoader.getResourceAsStream(info.superName.replace('.', '/') + ".class")) {
                if (superStream != null) {
                    this.loadClassInfo(info.superName, superStream);//加载父类
                }
            }
        }
        //五、递归解析接口
        for (String faces : info.interFaces) {
            try (InputStream superStream = this.resourceLoader.getResourceAsStream(faces.replace('.', '/') + ".class")) {
                if (superStream != null) {
                    this.loadClassInfo(faces, superStream);//加载父类
                }
            }
        }
        //六、类型链
        Set<String> castTypeList = new TreeSet<>();/* 可转换的类型 */
        String superName = info.superName;
        addCastTypeList(info, castTypeList);//this
        //
        if (superName != null) {
            while (superName != null && this.classInfoMap.containsKey(superName)) {
                ClassInfo superInfo = this.classInfoMap.get(superName);
                addCastTypeList(superInfo, castTypeList);//super
                superName = superInfo.superName;
            }
        }
        info.castType = castTypeList.toArray(new String[0]);
        //
        this.classInfoMap.put(info.className, info);
        return info;
    }

    private void addCastTypeList(final ClassInfo info, final Set<String> addTo) {
        if (info == null) {
            return;
        }
        addTo.add(info.className);
        if (info.superName != null) {
            addTo.add(info.superName);
        }
        if (info.interFaces != null) {
            for (String atFaces : info.interFaces) {
                addTo.add(atFaces);
                this.addCastTypeList(this.classInfoMap.get(atFaces), addTo);
            }
        }
    }
}