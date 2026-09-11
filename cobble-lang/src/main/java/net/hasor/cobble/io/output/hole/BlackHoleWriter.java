/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.io.output.hole;
import java.io.Writer;

/**
 * 黑洞 Writer
 * @author 赵永春 (zyc@hasor.net)
 * @version 2021-03-02
 */
public class BlackHoleWriter extends Writer {
    @Override
    public void write(char[] cbuf, int off, int len) {
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}
