/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.cobble.reflect;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.*;
import net.hasor.cobble.ArrayUtils;
import net.hasor.cobble.StringUtils;
import org.objectweb.asm.*;

/**
 * @author 赵永春 (zyc@hasor.net)
 */
public class Annotations {
    private final        Map<String, List<Annotation>> typeData;
    private final        Map<String, Annotations>      fieldData;
    private final        Map<String, Annotations>      methodData;
    private static final Annotations                   EMPTY = new Annotations();

    private Annotations() {
        this.typeData = new LinkedHashMap<>();
        this.fieldData = new LinkedHashMap<>();
        this.methodData = new LinkedHashMap<>();
    }

    public Annotation getAnnotation(Class<?> annoType) {
        if (annoType == null) {
            return null;
        } else {
            return getAnnotation(annoType.getName());
        }
    }

    public Annotation getAnnotation(String annoType) {
        if (annoType == null) {
            return null;
        }

        List<Annotation> annotations = this.typeData.get(annoType);
        if (annotations == null || annotations.isEmpty()) {
            return null;
        } else {
            return annotations.get(0);
        }
    }

    public List<Annotation> getAnnotations(Class<?> annoType) {
        if (annoType == null) {
            return null;
        } else {
            return getAnnotations(annoType.getName());
        }
    }

    public List<Annotation> getAnnotations(String annoType) {
        if (annoType == null) {
            return Collections.emptyList();
        }

        List<Annotation> annotations = this.typeData.get(annoType);
        if (annotations == null || annotations.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(annotations);
        }
    }

    public Annotations getField(String field) {
        if (StringUtils.isBlank(field)) {
            return null;
        } else {
            return this.fieldData.get(field);
        }
    }

    public Annotation getFieldAnnotation(String field, Class<?> annoType) {
        if (field == null || annoType == null) {
            return null;
        } else {
            return getFieldAnnotation(field, annoType.getName());
        }
    }

    public Annotation getFieldAnnotation(String field, String annoType) {
        if (StringUtils.isBlank(field)) {
            return null;
        }

        Annotations annotations = this.fieldData.get(field);
        if (annotations == null) {
            return null;
        } else {
            return annotations.getAnnotation(annoType);
        }
    }

    public List<Annotation> getFieldAnnotations(String field, Class<?> annoType) {
        if (field == null || annoType == null) {
            return null;
        } else {
            return getFieldAnnotations(field, annoType.getName());
        }
    }

    public List<Annotation> getFieldAnnotations(String field, String annoType) {
        if (StringUtils.isBlank(field)) {
            return Collections.emptyList();
        }

        Annotations annotations = this.fieldData.get(field);
        if (annotations == null) {
            return Collections.emptyList();
        } else {
            return annotations.getAnnotations(annoType);
        }
    }

    public Annotations getMethod(Method methodDesc) {
        if (methodDesc == null) {
            return null;
        } else {
            return getMethod(toMethodName(methodDesc.getName(), methodDesc.getParameterTypes()));
        }
    }

    public Annotations getMethod(String methodDesc) {
        if (StringUtils.isBlank(methodDesc)) {
            return null;
        } else {
            return this.methodData.get(methodDesc);
        }
    }

    public Annotation getMethodAnnotation(Method method, Class<?> annoType) {
        if (method == null || annoType == null) {
            return null;
        } else {
            String methodDesc = toMethodName(method.getName(), method.getParameterTypes());
            return getMethodAnnotation(methodDesc, annoType.getName());
        }
    }

    public Annotation getMethodAnnotation(Method method, String annoType) {
        if (method == null || StringUtils.isBlank(annoType)) {
            return null;
        } else {
            String methodDesc = toMethodName(method.getName(), method.getParameterTypes());
            return getMethodAnnotation(methodDesc, annoType);
        }
    }

    public Annotation getMethodAnnotation(String methodDesc, Class<?> annoType) {
        if (StringUtils.isBlank(methodDesc) || annoType == null) {
            return null;
        } else {
            return getMethodAnnotation(methodDesc, annoType.getName());
        }
    }

    public Annotation getMethodAnnotation(String methodDesc, String annoType) {
        if (StringUtils.isBlank(methodDesc)) {
            return null;
        }

        Annotations annotations = this.methodData.get(methodDesc);
        if (annotations == null) {
            return null;
        } else {
            return annotations.getAnnotation(annoType);
        }
    }

    public List<Annotation> getMethodAnnotations(Method method, Class<?> annoType) {
        if (method == null || annoType == null) {
            return null;
        } else {
            String methodDesc = toMethodName(method.getName(), method.getParameterTypes());
            return getMethodAnnotations(methodDesc, annoType.getName());
        }
    }

    public List<Annotation> getMethodAnnotations(Method method, String annoType) {
        if (method == null || StringUtils.isBlank(annoType)) {
            return null;
        } else {
            String methodDesc = toMethodName(method.getName(), method.getParameterTypes());
            return getMethodAnnotations(methodDesc, annoType);
        }
    }

    public List<Annotation> getMethodAnnotations(String methodDesc, String annoType) {
        if (StringUtils.isBlank(methodDesc)) {
            return Collections.emptyList();
        }

        Annotations annotations = this.methodData.get(methodDesc);
        if (annotations == null) {
            return Collections.emptyList();
        } else {
            return annotations.getAnnotations(annoType);
        }
    }

    public void putTypeData(String name, Annotation value) {
        this.typeData.computeIfAbsent(name, s -> new ArrayList<>()).add(value);
    }

    public void putFieldData(String name, Annotations value) {
        this.fieldData.put(name, value);
    }

    public void putMethodData(String method, Annotations value) {
        this.methodData.put(method, value);
    }

    void merge(Annotations other) {
        if (other == null) {
            return;
        }
        other.typeData.forEach((name, annotations) -> {
            this.typeData.computeIfAbsent(name, s -> new ArrayList<>()).addAll(annotations);
        });
        other.fieldData.forEach((name, annotations) -> {
            if (this.fieldData.containsKey(name)) {
                this.fieldData.get(name).merge(annotations);
            } else {
                this.fieldData.put(name, annotations);
            }
        });
        other.methodData.forEach((name, annotations) -> {
            if (this.methodData.containsKey(name)) {
                this.methodData.get(name).merge(annotations);
            } else {
                this.methodData.put(name, annotations);
            }
        });
    }

    /** 仅合并字段和方法注解，不合并类级注解 */
    void mergeMembers(Annotations other) {
        if (other == null) {
            return;
        }
        other.fieldData.forEach((name, annotations) -> {
            if (this.fieldData.containsKey(name)) {
                this.fieldData.get(name).merge(annotations);
            } else {
                this.fieldData.put(name, annotations);
            }
        });
        other.methodData.forEach((name, annotations) -> {
            if (this.methodData.containsKey(name)) {
                this.methodData.get(name).merge(annotations);
            } else {
                this.methodData.put(name, annotations);
            }
        });
    }

    boolean isEmpty() {
        return this.typeData.isEmpty() && this.fieldData.isEmpty() && this.methodData.isEmpty();
    }

    /** 实现对类，构造方法，字段，方法，参数 中的注解信息进行提取 */
    public static Annotations ofClass(Class<?> theClass) throws IOException {
        String packageName = theClass.getName().replace(".", "/");
        ClassLoader loader = theClass.getClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        InputStream asStream = loader.getResourceAsStream(packageName + ".class");
        if (asStream == null) {
            return EMPTY;
        }

        final Annotations typeData = new Annotations();
        ClassReader classReader = new ClassReader(asStream);
        classReader.accept(new ClassVisitor(Opcodes.ASM9) {
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                AnnotationVisitor av = super.visitAnnotation(desc, visible);
                return new ObjectAnnotationVisitor(api, av) {
                    @Override
                    public void visitEnd() {
                        typeData.putTypeData(ByteCodeTools.toJavaType(desc), this.data);
                        super.visitEnd();
                    }
                };
            }

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                final Annotations fieldData = new Annotations();
                return new FieldVisitor(api, super.visitField(access, name, descriptor, signature, value)) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                        AnnotationVisitor av = super.visitAnnotation(desc, visible);
                        return new ObjectAnnotationVisitor(api, av) {
                            @Override
                            public void visitEnd() {
                                fieldData.putTypeData(ByteCodeTools.toJavaType(desc), this.data);
                                super.visitEnd();
                            }
                        };
                    }

                    @Override
                    public void visitEnd() {
                        typeData.putFieldData(name, fieldData);
                        super.visitEnd();
                    }
                };
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                final String methodName = name + descriptor.substring(0, descriptor.indexOf(")") + 1);
                final Annotations methodData = new Annotations();
                return new MethodVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions)) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                        AnnotationVisitor av = super.visitAnnotation(desc, visible);
                        return new ObjectAnnotationVisitor(api, av) {
                            @Override
                            public void visitEnd() {
                                methodData.putTypeData(ByteCodeTools.toJavaType(desc), this.data);
                                super.visitEnd();
                            }
                        };
                    }

                    @Override
                    public void visitEnd() {
                        typeData.putMethodData(methodName, methodData);
                        super.visitEnd();
                    }
                };
            }

        }, ClassReader.SKIP_CODE);
        return typeData;
    }

    public static String toMethodName(String name, Class<?>... args) {
        return name + "(" + ByteCodeTools.toAsmType(args) + ")";
    }

    /** 解析类及其所有父类的注解信息，类级注解仅取目标类自身，字段和方法注解合并整个继承链 */
    public static Annotations ofClassHierarchy(Class<?> theClass) throws IOException {
        Annotations result = ofClass(theClass);
        Class<?> parent = theClass.getSuperclass();
        while (parent != null && parent != Object.class) {
            result.mergeMembers(ofClass(parent));
            parent = parent.getSuperclass();
        }
        return result;
    }

    public static String toConstructorName(Class<?>... args) {
        return "(" + ByteCodeTools.toAsmType(args) + ")";
    }

    public static Annotations merge(Annotations... args) {
        Annotations merged = new Annotations();
        for (Annotations item : args) {
            merged.merge(item);
        }
        return merged;
    }

    public static Annotations empty() {
        return EMPTY;
    }

    public static Annotations create() {
        return new Annotations();
    }

    private static class ObjectAnnotationVisitor extends AnnotationVisitor {
        protected final Annotation data = new Annotation();

        public ObjectAnnotationVisitor(int api, AnnotationVisitor av) {
            super(api, av);
        }

        @Override
        public void visit(String name, Object value) {
            if (value instanceof Type) {
                this.data.putData(name, ((Type) value).getClassName());
            } else if (value.getClass().isArray()) {
                this.data.putData(name, Arrays.asList(ArrayUtils.safeToObject(value)));
            } else {
                this.data.putData(name, value.toString());
            }
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            this.data.putData(name, value);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            AnnotationVisitor av = super.visitArray(name);
            return new ArrayDefaultVisitor(Opcodes.ASM9, av, name, this.data);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            AnnotationVisitor av = super.visitAnnotation(name, descriptor);
            Annotation localData = this.data;
            return new ObjectAnnotationVisitor(Opcodes.ASM9, av) {
                @Override
                public void visitEnd() {
                    if (StringUtils.isNotBlank(name)) {
                        localData.putData(name, this.data);
                    } else {
                        localData.putData(ByteCodeTools.toJavaType(descriptor), this.data);
                    }
                    super.visitEnd();
                }
            };
        }
    }

    private static class ArrayDefaultVisitor extends AnnotationVisitor {
        protected final String     name;
        protected final Annotation data;

        protected ArrayDefaultVisitor(int api, AnnotationVisitor av, String name, Annotation data) {
            super(api, av);
            this.name = name;
            this.data = data;
        }

        @Override
        public void visit(String name, Object value) {
            if (value instanceof Type) {
                this.data.putData(this.name, ((Type) value).getClassName());
            } else {
                this.data.putData(this.name, Arrays.asList(ArrayUtils.safeToObject(value)));
            }
        }

        @Override
        public void visitEnum(String name, String descriptor, String value) {
            this.data.putData(this.name, value);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String descriptor) {
            AnnotationVisitor av = super.visitAnnotation(name, descriptor);
            String localName = this.name;
            Annotation localData = this.data;
            return new ObjectAnnotationVisitor(Opcodes.ASM9, av) {
                @Override
                public void visitEnd() {
                    if (StringUtils.isNotBlank(localName)) {
                        localData.putData(localName, this.data);
                    } else {
                        localData.putData(ByteCodeTools.toJavaType(descriptor), this.data);
                    }
                    super.visitEnd();
                }
            };
        }
    }
}
