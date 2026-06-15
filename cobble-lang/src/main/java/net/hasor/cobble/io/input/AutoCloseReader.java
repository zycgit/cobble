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
package net.hasor.cobble.io.input;
import java.io.IOException;
import java.io.Reader;
import java.lang.ref.Cleaner;
import net.hasor.cobble.io.IOUtils;

/**
 * 具有自动关闭的输入流
 * @author 赵永春 (zyc@hasor.net)
 * @version 2009-5-13
 */
public class AutoCloseReader extends ProxyReader {
    private static final Cleaner CLEANER = Cleaner.create();
    private final        Cleaner.Cleanable cleanable;

    /**
     * Creates an automatically closing proxy for the given input stream.
     * @param in underlying input stream
     */
    public AutoCloseReader(final Reader in) {
        super(in);
        this.cleanable = CLEANER.register(this, new ReaderCleanup(in));
    }

    /**
     * Closes the underlying input stream and replaces the reference to it
     * with a {@link ClosedReader} instance.
     * <p>
     * This method is automatically called by the read methods when the end
     * of input has been reached.
     * <p>
     * Note that it is safe to call this method any number of times. The original
     * underlying input stream is closed and discarded only once when this
     * method is first called.
     * @throws IOException if the underlying input stream can not be closed
     */
    @Override
    public void close() throws IOException {
        cleanable.clean();
        in = new ClosedReader();
    }

    /**
     * Automatically closes the stream if the end of stream was reached.
     * @param n number of bytes read, or -1 if no more bytes are available
     * @throws IOException if the stream could not be closed
     * @since 2.0
     */
    @Override
    protected void afterRead(final int n) throws IOException {
        if (n == IOUtils.EOF) {
            close();
        }
    }

    private static final class ReaderCleanup implements Runnable {
        private Reader in;

        private ReaderCleanup(Reader in) {
            this.in = in;
        }

        @Override
        public void run() {
            Reader target = this.in;
            this.in = null;
            if (target == null) {
                return;
            }
            try {
                target.close();
            } catch (IOException ignored) {
                // best-effort cleanup
            }
        }
    }
}
