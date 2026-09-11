/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.io.output;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * 使用OutputStream输出Writer的工具类
 * @author 赵永春 (zyc@hasor.net)
 * @version 2009-5-13
 */
public class WriterOutputStream extends OutputStream {
    private Writer writer   = null;
    private       String encoding = null;
    private final byte[] buf      = new byte[1];
    //========================================================================================

    /**
     * 带Writer和字符编码格式参数的构造函数
     * @param writer - OutputStream使用的Reader
     * @param encoding - OutputStream使用的字符编码格式。
     */
    public WriterOutputStream(final Writer writer, final String encoding) {
        this.writer = writer;
        this.encoding = encoding;
    }

    /**
     * 带Writer参数构造函数
     * @param writer - OutputStream使用的Writer
     */
    public WriterOutputStream(final Writer writer) {
        this.writer = writer;
    }
    //========================================================================================

    /** @see OutputStream#close() */
    @Override
    public void close() throws IOException {
        this.writer.close();
        this.writer = null;
        this.encoding = null;
    }

    /** @see OutputStream#flush() */
    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }

    /** @see OutputStream#write(byte[]) */
    @Override
    public void write(final byte[] b) throws IOException {
        if (this.encoding == null) {
            this.writer.write(new String(b));
        } else {
            this.writer.write(new String(b, this.encoding));
        }
    }

    /** @see OutputStream#write(byte[], int, int) */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.encoding == null) {
            this.writer.write(new String(b, off, len));
        } else {
            this.writer.write(new String(b, off, len, this.encoding));
        }
    }

    /** @see OutputStream#write(int) */
    @Override
    public synchronized void write(final int b) throws IOException {
        this.buf[0] = (byte) b;
        this.write(this.buf);
    }
}
