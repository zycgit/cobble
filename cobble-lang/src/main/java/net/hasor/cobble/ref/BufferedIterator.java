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
package net.hasor.cobble.ref;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * 通常使用 Iterator 只能单方向一直迭代下去，使用 BufferedIterator 允许迭代器 mark 一个位置。
 * 并在允许的缓冲范围内随时回退到 mark 位置上。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2016-07-17
 */
public class BufferedIterator<T> implements Iterator<T> {
    private final Iterator<T>   dataIterator;
    private final LinkedList<T> bufferedData;
    private final int           bufferedSize;
    private       int           readIndex = 0;
    private       boolean       marked    = false;

    public BufferedIterator(Iterator<T> iterator) {
        this(iterator, 4096);
    }

    public BufferedIterator(Iterator<T> dataIterator, int bufferedSize) {
        this.dataIterator = dataIterator;
        this.bufferedSize = bufferedSize;
        this.bufferedData = new LinkedList<>();
    }

    // reset readIndex to start mark.
    public void reset() {
        if (!this.marked) {
            throw new IllegalStateException("no marked to reset.");
        }
        this.readIndex = 0;
    }

    // set or reset new ,mark.
    public void mark() {
        this.release();
        this.marked = true;
    }

    // release mark status, and release bufferedData.
    public void release() {
        this.marked = false;
        this.bufferedData.clear();
        this.readIndex = 0;
    }

    protected void checkSize() {
        if (this.readIndex >= this.bufferedSize) {
            throw new IndexOutOfBoundsException("out of buffered size " + this.bufferedSize);
        }
    }

    @Override
    public boolean hasNext() {
        if (this.marked) {
            return this.readIndex < this.bufferedData.size() || this.dataIterator.hasNext();
        } else {
            return this.dataIterator.hasNext();
        }
    }

    @Override
    public T next() {
        if (this.marked) {
            if (this.readIndex < this.bufferedData.size()) {
                try {
                    return this.bufferedData.get(this.readIndex);
                } finally {
                    this.readIndex++;
                }
            } else {
                checkSize();
                if (this.dataIterator.hasNext()) {
                    T nextData = this.dataIterator.next();
                    this.bufferedData.addLast(nextData);
                    this.readIndex++;
                    return nextData;
                } else {
                    throw new NoSuchElementException("no more data.");
                }
            }

        } else {
            return this.dataIterator.next();
        }
    }
}
