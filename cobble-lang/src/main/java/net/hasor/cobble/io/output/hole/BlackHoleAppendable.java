/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE.txt file for the full license.
 * https://www.apache.org/licenses/LICENSE-2.0
 */
package net.hasor.cobble.io.output.hole;
/**
 * 黑洞 Appendable
 * @author 赵永春 (zyc@hasor.net)
 * @version 2021-03-02
 */
public class BlackHoleAppendable implements Appendable {
    @Override
    public Appendable append(CharSequence csq) {
        return this;
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) {
        return this;
    }

    @Override
    public Appendable append(char c) {
        return this;
    }
}
