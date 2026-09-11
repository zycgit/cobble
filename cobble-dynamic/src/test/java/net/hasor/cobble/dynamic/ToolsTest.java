/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.dynamic;
import java.lang.reflect.Method;
import java.util.function.Predicate;
import net.hasor.cobble.dynamic.anno.custom.MyAop;
import net.hasor.cobble.dynamic.beans.extend.GrandFatherBean;
import net.hasor.cobble.dynamic.beans.extend.JamesBean;
import org.junit.Test;

public class ToolsTest {
    @Test
    public void toolsTest1() {
        String[] asmType1 = AsmTools.splitAsmType("IIIILLjava/lang/Integer;");
        assert asmType1[0].equals("I");
        assert asmType1[1].equals("I");
        assert asmType1[2].equals("I");
        assert asmType1[3].equals("I");
        assert asmType1[4].equals("LLjava/lang/Integer;");
        //
        String[] asmType2 = AsmTools.splitAsmType("");
        assert asmType2.length == 0;
    }

    @Test
    public void toolsTest2() {
        Predicate<Class<?>> annotatedWithClass = Matchers.annotatedWithClass(MyAop.class);
        Predicate<Method> annotatedWithMethod = Matchers.annotatedWithMethod(MyAop.class);
        //        Predicate<Class<?>> expressionClass = AopMatchers.expressionClass("net.test.hasor.*");
        //        Predicate<Method> expressionMethod = AopMatchers.expressionMethod("java.lang.String *.*(*)");
        Predicate<Class<?>> subClassesOf = Matchers.subClassesOf(JamesBean.class);
        //
        annotatedWithClass.hashCode();
        annotatedWithMethod.hashCode();
        //        expressionClass.hashCode();
        //        expressionMethod.hashCode();
        subClassesOf.hashCode();
        //
        assert annotatedWithClass.toString().startsWith("ClassAnnotationOf");
        assert annotatedWithClass.equals(Matchers.annotatedWithClass(MyAop.class));
        assert !annotatedWithClass.equals(Matchers.annotatedWithClass(Aop.class));
        //
        assert annotatedWithMethod.toString().startsWith("MethodAnnotationOf");
        assert annotatedWithMethod.equals(Matchers.annotatedWithMethod(MyAop.class));
        assert !annotatedWithMethod.equals(Matchers.annotatedWithClass(Aop.class));
        //
        //        assert expressionClass.toString().startsWith("ClassOf");
        //        assert expressionClass.equals(expressionClass);
        //        assert !expressionClass.equals(expressionMethod);
        //        assert expressionClass.equals(AopMatchers.expressionClass("net.test.hasor.*"));
        //        //
        //        assert expressionMethod.toString().startsWith("MethodOf");
        //        assert expressionMethod.equals(expressionMethod);
        //        assert !expressionMethod.equals(expressionClass);
        //        assert expressionMethod.equals(AopMatchers.expressionMethod("java.lang.String *.*(*)"));
        //
        assert subClassesOf.toString().startsWith("SubClassesOf");
        assert subClassesOf.equals(Matchers.subClassesOf(JamesBean.class));
        assert !subClassesOf.equals(Matchers.subClassesOf(GrandFatherBean.class));
        //
    }
}
