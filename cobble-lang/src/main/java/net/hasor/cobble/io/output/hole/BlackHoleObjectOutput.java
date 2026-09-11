/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.io.output.hole;
import java.io.ObjectOutput;

/**
 * 黑洞 DataOutput
 * @author 赵永春 (zyc@hasor.net)
 * @version 2021-03-02
 */
public class BlackHoleObjectOutput implements ObjectOutput {
    @Override
    public void writeObject(Object obj) {
    }

    @Override
    public void write(int b) {
    }

    @Override
    public void write(byte[] b) {
    }

    @Override
    public void write(byte[] b, int off, int len) {
    }

    @Override
    public void writeBoolean(boolean v) {
    }

    @Override
    public void writeByte(int v) {
    }

    @Override
    public void writeShort(int v) {
    }

    @Override
    public void writeChar(int v) {
    }

    @Override
    public void writeInt(int v) {
    }

    @Override
    public void writeLong(long v) {
    }

    @Override
    public void writeFloat(float v) {
    }

    @Override
    public void writeDouble(double v) {
    }

    @Override
    public void writeBytes(String s) {
    }

    @Override
    public void writeChars(String s) {
    }

    @Override
    public void writeUTF(String s) {
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}
