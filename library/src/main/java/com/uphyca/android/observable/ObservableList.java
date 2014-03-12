/*
 * Copyright (C) 2014 uPhyca Inc.
 *
 * Copyright (C) 2010 The Android Open Source Project
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
import android.database.DataSetObserver;

import java.io.Closeable;
import java.util.List;

/**
 * A list that allows observers to track changes when they occur.
 * 
 * @param <E> the list element type
 * @author Sosuke Masui (masui@uphyca.com)
 */
public interface ObservableList<E> extends List<E>, Closeable {

    /**
     * Closes the List, releasing all of its resources and making it completely invalid.
     */
    void close();

    /**
     * return true if the list is closed
     * 
     * @return true if the list is closed.
     */
    boolean isClosed();

    /**
     * Register an observer that is called when changes happen to the content backing this list.
     * 
     * @param observer the object that gets notified when the content backing the list changes.
     * @see #unregisterContentObserver(android.database.ContentObserver)
     */
    void registerContentObserver(ContentObserver observer);

    /**
     * Unregister an observer that has previously been registered with this list via {@link #registerContentObserver}.
     * 
     * @param observer the object to unregister.
     * @see #registerContentObserver(ContentObserver)
     */
    void unregisterContentObserver(ContentObserver observer);

    /**
     * Register an observer that is called when changes happen to the contents of the this lists data set.
     * 
     * @param observer the object that gets notified when the lists data set changes.
     * @see #unregisterDataSetObserver(android.database.DataSetObserver)
     */
    void registerDataSetObserver(DataSetObserver observer);

    /**
     * Unregister an observer that has previously been registered with this list via {@link #registerContentObserver}.
     * 
     * @param observer the object to unregister.
     * @see #registerDataSetObserver(DataSetObserver)
     */
    void unregisterDataSetObserver(DataSetObserver observer);
}
