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
import net.hasor.cobble.reflect.ByteCodeTools;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 生成字节码时候使用的工具类。
 * @author 赵永春 (zyc@hasor.net)
 * @version 2009-10-16
 */
class AsmTools extends ByteCodeTools {
    /** 根据类型获取其Return指令 */
    public static int getReturn(final String asmType) {
        char t = asmType.charAt(0);
        switch (t) {
            case 'B':
                return Opcodes.IRETURN;//Byte
            case 'C':
                return Opcodes.IRETURN;//Char
            case 'D':
                return Opcodes.DRETURN;//Double
            case 'F':
                return Opcodes.FRETURN;//Float
            case 'I':
                return Opcodes.IRETURN;//Integer
            case 'J':
                return Opcodes.LRETURN;//Long
            case 'L':
                return Opcodes.ARETURN;//Ref
            case 'S':
                return Opcodes.IRETURN;//Short
            case 'Z':
                return Opcodes.IRETURN;//Boolean
            case '[':
                return Opcodes.ARETURN;//Array
            case 'V':
                return Opcodes.RETURN;//Void
            default:
                throw new UnsupportedOperationException("Unsupported LOAD instruction.");//
        }
    }

    /** 根据类型获取其Load指令 */
    public static int getLoad(final String asmType) {
        char t = asmType.charAt(0);
        switch (t) {
            case 'B':
                return Opcodes.ILOAD;//Byte
            case 'C':
                return Opcodes.ILOAD;//Char
            case 'D':
                return Opcodes.DLOAD;//Double
            case 'F':
                return Opcodes.FLOAD;//Float
            case 'I':
                return Opcodes.ILOAD;//Integer
            case 'J':
                return Opcodes.LLOAD;//Long
            case 'L':
                return Opcodes.ALOAD;//Ref
            case 'S':
                return Opcodes.ILOAD;//Short
            case 'Z':
                return Opcodes.ILOAD;//Boolean
            case '[':
                return Opcodes.ALOAD;//Array
            default:
                throw new UnsupportedOperationException("Unsupported LOAD instruction.");//
        }
    }

    /** 根据asm类型获取其ASTORE指令 */
    /*public static int getAstore(final String asmType) {
        char t = asmType.charAt(0);
        switch (t) {
        case 'B':
            return Opcodes.IASTORE;//Byte
        case 'C':
            return Opcodes.IASTORE;//Char
        case 'D':
            return Opcodes.DASTORE;//Double
        case 'F':
            return Opcodes.FASTORE;//Float
        case 'I':
            return Opcodes.IASTORE;//Integer
        case 'J':
            return Opcodes.LASTORE;//Long
        case 'L':
            return Opcodes.AASTORE;//Ref
        case 'S':
            return Opcodes.IASTORE;//Short
        case 'Z':
            return Opcodes.IASTORE;//Boolean
        case '[':
            return Opcodes.AASTORE;//Array
        default:
            throw new UnsupportedOperationException("不支持的类型装载请求");//
        }
    }*/

    /** 通过位运算决定check是否在data里 */
    public static boolean checkAnd(final int data, int... check) {
        for (int checkItem : check) {
            int or = data | checkItem;
            if (or != data) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkOr(final int data, int... check) {
        for (int checkItem : check) {
            int or = data | checkItem;
            if (or == data) {
                return true;
            }
        }
        return false;
    }

    //Code Builder “new Object[] { abc, abcc, abcc };”
    public static void codeBuilder_1(MethodVisitor mv, String[] asmParams, Map<String, Integer> paramIndexMap) {
        Set<String> typeEnum = new HashSet<>(Arrays.asList("B", "S", "I", "J", "F", "D", "C", "Z"));
        int paramCount = asmParams.length;
        mv.visitIntInsn(Opcodes.BIPUSH, paramCount);
        mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
        for (int i = 0; i < paramCount; i++) {
            String asmType = asmParams[i];
            mv.visitInsn(Opcodes.DUP);
            mv.visitIntInsn(Opcodes.BIPUSH, i);
            if (typeEnum.contains(asmParams[i])) {
                mv.visitVarInsn(AsmTools.getLoad(asmType), paramIndexMap.get("args" + i));
                codeBuilder_valueOf(mv, asmParams[i]);
            } else {
                mv.visitVarInsn(Opcodes.ALOAD, paramIndexMap.get("args" + i));
            }
            mv.visitInsn(Opcodes.AASTORE);
        }
    }

    //Code Builder “Double.valueOf(xxx);”
    public static void codeBuilder_valueOf(MethodVisitor mv, String asmType) {
        if (asmType.equals("B")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
        } else if (asmType.equals("S")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
        } else if (asmType.equals("I")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        } else if (asmType.equals("J")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if (asmType.equals("F")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if (asmType.equals("D")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        } else if (asmType.equals("C")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        } else if (asmType.equals("Z")) {
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        }
    }

    //Code Builder “new Class[] { int.class, Object.class, boolean.class, short.class };”
    public static void codeBuilder_2(MethodVisitor mv, String[] asmParams) {
        int paramCount = asmParams.length;
        mv.visitIntInsn(BIPUSH, paramCount);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
        for (int i = 0; i < paramCount; i++) {
            String asmType = asmParams[i];
            mv.visitInsn(Opcodes.DUP);
            mv.visitIntInsn(Opcodes.BIPUSH, i);
            if (asmParams[i].equals("B")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("S")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("I")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("J")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("F")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("D")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("C")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
            } else if (asmParams[i].equals("Z")) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
            } else {
                mv.visitLdcInsn(Type.getType(asmType));//  Ljava/lang/Object;
            }
            mv.visitInsn(Opcodes.AASTORE);
        }
    }

    //Code Builder “return ...”
    public static void codeBuilder_3(MethodVisitor mv, String asmReturns) {
        codeBuilder_3(mv, asmReturns, null);
    }

    //Code Builder “return ...”
    public static void codeBuilder_3(MethodVisitor mv, String asmReturns, Label tryEnd) {
        codeBuilder_Cast(mv, asmReturns, tryEnd);
        mv.visitInsn(AsmTools.getReturn(asmReturns));
    }

    //Code Builder “(Object) ...”
    public static void codeBuilder_Cast(MethodVisitor mv, String asmReturns, Label tryEnd) {
        if (asmReturns.equals("B")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("S")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("I")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("J")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("F")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("D")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("C")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("Z")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else if (asmReturns.equals("V")) {
            mv.visitInsn(Opcodes.POP);
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        } else {
            mv.visitTypeInsn(Opcodes.CHECKCAST, AsmTools.asmTypeToType(asmReturns));
            if (tryEnd != null) {
                mv.visitLabel(tryEnd);
            }
        }
    }
}
