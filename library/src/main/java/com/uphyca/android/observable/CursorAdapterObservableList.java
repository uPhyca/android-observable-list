/*
 * Copyright (C) 2014 uPhyca Inc.
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

package com.uphyca.android.observable;

import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;

import java.util.AbstractList;

/**
 * A Cursor backed ObservableList.
 * 
 * @param <E> the list element type
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class CursorAdapterObservableList<E> extends AbstractList<E> implements ObservableList<E> {

    /**
     * Maps cursor columns to a desired element
     * 
     * @param <E> the list element type
     */
    public interface Mapper<E> {
        E convert(Cursor cursor);
    }

    private final Cursor mCursor;
    private final Mapper<E> mMapper;
    private final Object[] mLock = new Object[0];

    public CursorAdapterObservableList(Cursor cursor, Mapper<E> mapper) {
        if (cursor == null || mapper == null) {
            throw new IllegalArgumentException();
        }
        mCursor = cursor;
        mMapper = mapper;
    }

    @Override
    public void close() {
        synchronized (mLock) {
            mCursor.close();
        }
    }

    @Override
    public boolean isClosed() {
        synchronized (mLock) {
            return mCursor.isClosed();
        }
    }

    @Override
    public E get(int location) {
        synchronized (mLock) {
            if (location < 0 || location >= size()) {
                throwIndexOutOfBoundsException(location, size());
            }
            if (mCursor.moveToPosition(location)) {
                return mMapper.convert(mCursor);
            }
            return null;
        }
    }

    @Override
    public int size() {
        synchronized (mLock) {
            return mCursor.getCount();
        }
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {
        mCursor.registerContentObserver(observer);
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
        mCursor.unregisterContentObserver(observer);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mCursor.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mCursor.unregisterDataSetObserver(observer);
    }

    static IndexOutOfBoundsException throwIndexOutOfBoundsException(int index, int size) {
        throw new IndexOutOfBoundsException("Invalid index " + index + ", size is " + size);
    }
}
