/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.hasor.cobble.reflect.resolvable;
import net.hasor.cobble.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Helper class that encapsulates the specification of a method parameter, i.e. a {@link Method}
 * or {@link Constructor} plus a parameter index and a nested type index for a declared generic
 * type. Useful as a specification object to pass along.
 *
 * subclass available which synthesizes annotations with attribute aliases. That subclass is used
 * for web and message endpoint processing, in particular.
 *
 * 2021-11-01 reduction based on SpringFramework, org.springframework.core.MethodParameter
 *
 * @author 赵永春 (zyc@hasor.net)
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Andy Clement
 * @author Sam Brannen
 * @author Sebastien Deleuze
 * @author Phillip Webb
 * @since 2.0
 */
class MethodParameter {
    private static final Annotation[] EMPTY_ANNOTATION_ARRAY = new Annotation[0];
    private final        Executable   executable;
    private final        int          parameterIndex;
    private volatile     Parameter    parameter;
    private              int          nestingLevel;
    /** Map from Integer level to Integer type index. */
    Map<Integer, Integer> typeIndexesPerLevel;
    /** The containing class. Could also be supplied by overriding {@link #getContainingClass()} */
    private volatile Class<?>        containingClass;
    private volatile Class<?>        parameterType;
    private volatile Type            genericParameterType;
    private volatile Annotation[]    parameterAnnotations;
    private volatile String          parameterName;
    private volatile MethodParameter nestedMethodParameter;

    /**
     * Create a new {@code MethodParameter} for the given method, with nesting level 1.
     * @param method the Method to specify a parameter for
     * @param parameterIndex the index of the parameter: -1 for the method
     * return type; 0 for the first method parameter; 1 for the second method
     * parameter, etc.
     */
    public MethodParameter(Method method, int parameterIndex) {
        this(method, parameterIndex, 1);
    }

    /**
     * Create a new {@code MethodParameter} for the given method.
     * @param method the Method to specify a parameter for
     * @param parameterIndex the index of the parameter: -1 for the method
     * return type; 0 for the first method parameter; 1 for the second method
     * parameter, etc.
     * @param nestingLevel the nesting level of the target type
     * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
     * nested List, whereas 2 would indicate the element of the nested List)
     */
    public MethodParameter(Method method, int parameterIndex, int nestingLevel) {
        Objects.requireNonNull(method, "Method must not be null");
        this.executable = method;
        this.parameterIndex = validateIndex(method, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    /**
     * Create a new MethodParameter for the given constructor, with nesting level 1.
     * @param constructor the Constructor to specify a parameter for
     * @param parameterIndex the index of the parameter
     */
    public MethodParameter(Constructor<?> constructor, int parameterIndex) {
        this(constructor, parameterIndex, 1);
    }

    /**
     * Create a new MethodParameter for the given constructor.
     * @param constructor the Constructor to specify a parameter for
     * @param parameterIndex the index of the parameter
     * @param nestingLevel the nesting level of the target type
     * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
     * nested List, whereas 2 would indicate the element of the nested List)
     */
    public MethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
        Objects.requireNonNull(constructor, "Constructor must not be null");
        this.executable = constructor;
        this.parameterIndex = validateIndex(constructor, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    /**
     * Internal constructor used to create a {@link MethodParameter} with a
     * containing class already set.
     * @param executable the Executable to specify a parameter for
     * @param parameterIndex the index of the parameter
     * @param containingClass the containing class
     * @since 5.2
     */
    MethodParameter(Executable executable, int parameterIndex, Class<?> containingClass) {
        Objects.requireNonNull(executable, "Executable must not be null");
        this.executable = executable;
        this.parameterIndex = validateIndex(executable, parameterIndex);
        this.nestingLevel = 1;
        this.containingClass = containingClass;
    }

    /**
     * Copy constructor, resulting in an independent MethodParameter object
     * based on the same metadata and cache state that the original object was in.
     * @param original the original MethodParameter object to copy from
     */
    public MethodParameter(MethodParameter original) {
        Objects.requireNonNull(original, "Original must not be null");
        this.executable = original.executable;
        this.parameterIndex = original.parameterIndex;
        this.parameter = original.parameter;
        this.nestingLevel = original.nestingLevel;
        this.typeIndexesPerLevel = original.typeIndexesPerLevel;
        this.containingClass = original.containingClass;
        this.parameterType = original.parameterType;
        this.genericParameterType = original.genericParameterType;
        this.parameterAnnotations = original.parameterAnnotations;
        this.parameterName = original.parameterName;
    }

    /**
     * Return the wrapped Method, if any.
     * <p>Note: Either Method or Constructor is available.
     * @return the Method, or {@code null} if none
     */
    public Method getMethod() {
        return (this.executable instanceof Method ? (Method) this.executable : null);
    }

    /**
     * Return the wrapped Constructor, if any.
     * <p>Note: Either Method or Constructor is available.
     * @return the Constructor, or {@code null} if none
     */
    public Constructor<?> getConstructor() {
        return (this.executable instanceof Constructor ? (Constructor<?>) this.executable : null);
    }

    /** Return the class that declares the underlying Method or Constructor. */
    public Class<?> getDeclaringClass() {
        return this.executable.getDeclaringClass();
    }

    /**
     * Return the wrapped executable.
     * @return the Method or Constructor as Executable
     * @since 5.0
     */
    public Executable getExecutable() {
        return this.executable;
    }

    /**
     * Return the {@link Parameter} descriptor for method/constructor parameter.
     * @since 5.0
     */
    public Parameter getParameter() {
        if (this.parameterIndex < 0) {
            throw new IllegalStateException("Cannot retrieve Parameter descriptor for method return type");
        }
        Parameter parameter = this.parameter;
        if (parameter == null) {
            parameter = getExecutable().getParameters()[this.parameterIndex];
            this.parameter = parameter;
        }
        return parameter;
    }

    /**
     * Return the index of the method/constructor parameter.
     * @return the parameter index (-1 in case of the return type)
     */
    public int getParameterIndex() {
        return this.parameterIndex;
    }

    /**
     * Return the nesting level of the target type
     * (typically 1; e.g. in case of a List of Lists, 1 would indicate the
     * nested List, whereas 2 would indicate the element of the nested List).
     */
    public int getNestingLevel() {
        return this.nestingLevel;
    }

    /**
     * Obtain the (lazily constructed) type-indexes-per-level Map.
     */
    private Map<Integer, Integer> getTypeIndexesPerLevel() {
        if (this.typeIndexesPerLevel == null) {
            this.typeIndexesPerLevel = new HashMap<>(4);
        }
        return this.typeIndexesPerLevel;
    }

    /**
     * Return a variant of this {@code MethodParameter} which points to the
     * same parameter but one nesting level deeper.
     * @since 4.3
     */
    public MethodParameter nested() {
        return nested(null);
    }

    /**
     * Return a variant of this {@code MethodParameter} which points to the
     * same parameter but one nesting level deeper.
     * @param typeIndex the type index for the new nesting level
     * @since 5.2
     */
    public MethodParameter nested(Integer typeIndex) {
        MethodParameter nestedParam = this.nestedMethodParameter;
        if (nestedParam != null && typeIndex == null) {
            return nestedParam;
        }
        nestedParam = nested(this.nestingLevel + 1, typeIndex);
        if (typeIndex == null) {
            this.nestedMethodParameter = nestedParam;
        }
        return nestedParam;
    }

    private MethodParameter nested(int nestingLevel, Integer typeIndex) {
        MethodParameter copy = clone();
        copy.nestingLevel = nestingLevel;
        if (this.typeIndexesPerLevel != null) {
            copy.typeIndexesPerLevel = new HashMap<>(this.typeIndexesPerLevel);
        }
        if (typeIndex != null) {
            copy.getTypeIndexesPerLevel().put(copy.nestingLevel, typeIndex);
        }
        copy.parameterType = null;
        copy.genericParameterType = null;
        return copy;
    }

    /**
     * Return whether this method indicates a parameter which is not required:
     * either in the form of Java 8's {@link java.util.Optional}, any variant
     * of a parameter-level {@code Nullable} annotation (such as from JSR-305
     * or the FindBugs set of annotations), or a language-level nullable type
     * declaration or {@code Continuation} parameter in Kotlin.
     * @since 4.3
     */
    public boolean isOptional() {
        return (getParameterType() == Optional.class || hasNullableAnnotation());
    }

    /**
     * Check whether this method parameter is annotated with any variant of a
     * {@code Nullable} annotation, e.g. {@code javax.annotation.Nullable} or
     * {@code edu.umd.cs.findbugs.annotations.Nullable}.
     */
    private boolean hasNullableAnnotation() {
        for (Annotation ann : getParameterAnnotations()) {
            if ("Nullable".equals(ann.annotationType().getSimpleName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the containing class for this method parameter.
     * @return a specific containing class (potentially a subclass of the
     * declaring class), or otherwise simply the declaring class itself
     * @see #getDeclaringClass()
     */
    public Class<?> getContainingClass() {
        Class<?> containingClass = this.containingClass;
        return (containingClass != null ? containingClass : getDeclaringClass());
    }

    /**
     * Return the type of the method/constructor parameter.
     * @return the parameter type (never {@code null})
     */
    public Class<?> getParameterType() {
        Class<?> paramType = this.parameterType;
        if (paramType != null) {
            return paramType;
        }
        if (getContainingClass() != getDeclaringClass()) {
            paramType = ResolvableType.forMethodParameter(this, null, 1).resolve();
        }
        if (paramType == null) {
            paramType = computeParameterType();
        }
        this.parameterType = paramType;
        return paramType;
    }

    /**
     * Return the generic type of the method/constructor parameter.
     * @return the parameter type (never {@code null})
     * @since 3.0
     */
    public Type getGenericParameterType() {
        Type paramType = this.genericParameterType;
        if (paramType == null) {
            if (this.parameterIndex < 0) {
                Method method = getMethod();
                paramType = (method != null ? method.getGenericReturnType() : void.class);
            } else {
                Type[] genericParameterTypes = this.executable.getGenericParameterTypes();
                int index = this.parameterIndex;
                if (this.executable instanceof Constructor && ClassUtils.isInnerClass(this.executable.getDeclaringClass()) && genericParameterTypes.length == this.executable.getParameterCount() - 1) {
                    // Bug in javac: type array excludes enclosing instance parameter
                    // for inner classes with at least one generic constructor parameter,
                    // so access it with the actual parameter index lowered by 1
                    index = this.parameterIndex - 1;
                }
                paramType = (index >= 0 && index < genericParameterTypes.length ? genericParameterTypes[index] : computeParameterType());
            }
            this.genericParameterType = paramType;
        }
        return paramType;
    }

    private Class<?> computeParameterType() {
        if (this.parameterIndex < 0) {
            Method method = getMethod();
            if (method == null) {
                return void.class;
            }

            return method.getReturnType();
        }
        return this.executable.getParameterTypes()[this.parameterIndex];
    }

    /**
     * Return the annotations associated with the specific method/constructor parameter.
     */
    public Annotation[] getParameterAnnotations() {
        Annotation[] paramAnns = this.parameterAnnotations;
        if (paramAnns == null) {
            Annotation[][] annotationArray = this.executable.getParameterAnnotations();
            int index = this.parameterIndex;
            if (this.executable instanceof Constructor && ClassUtils.isInnerClass(this.executable.getDeclaringClass()) && annotationArray.length == this.executable.getParameterCount() - 1) {
                // Bug in javac in JDK <9: annotation array excludes enclosing instance parameter
                // for inner classes, so access it with the actual parameter index lowered by 1
                index = this.parameterIndex - 1;
            }
            paramAnns = (index >= 0 && index < annotationArray.length ? adaptAnnotationArray(annotationArray[index]) : EMPTY_ANNOTATION_ARRAY);
            this.parameterAnnotations = paramAnns;
        }
        return paramAnns;
    }

    /**
     * A template method to post-process a given annotation array before
     * returning it to the caller.
     * <p>The default implementation simply returns the given annotation array as-is.
     * @param annotations the annotation array about to be returned
     * @return the post-processed annotation array (or simply the original one)
     * @since 4.2
     */
    protected Annotation[] adaptAnnotationArray(Annotation[] annotations) {
        return annotations;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodParameter)) {
            return false;
        }
        MethodParameter otherParam = (MethodParameter) other;
        return (getContainingClass() == otherParam.getContainingClass() && Objects.deepEquals(this.typeIndexesPerLevel, otherParam.typeIndexesPerLevel) && this.nestingLevel == otherParam.nestingLevel && this.parameterIndex == otherParam.parameterIndex && this.executable.equals(otherParam.executable));
    }

    @Override
    public int hashCode() {
        return (31 * this.executable.hashCode() + this.parameterIndex);
    }

    @Override
    public String toString() {
        Method method = getMethod();
        return (method != null ? "method '" + method.getName() + "'" : "constructor") + " parameter " + this.parameterIndex;
    }

    @Override
    public MethodParameter clone() {
        return new MethodParameter(this);
    }

    private static int validateIndex(Executable executable, int parameterIndex) {
        int count = executable.getParameterCount();
        if (!(parameterIndex >= -1 && parameterIndex < count)) {
            throw new IllegalStateException("Parameter index needs to be between -1 and " + (count - 1));
        }
        return parameterIndex;
    }
}
