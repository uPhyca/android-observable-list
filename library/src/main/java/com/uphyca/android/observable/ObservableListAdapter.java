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

import android.content.Context;
import android.database.ContentObserver;
import android.database.DataSetObserver;
import android.os.Handler;
import android.widget.ArrayAdapter;

/**
 * A concrete BaseAdapter that is backed by an ObservableList of arbitrary objects.
 *
 * @param <T> the list element type
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class ObservableListAdapter<T> extends ArrayAdapter<T> {

    protected boolean mDataValid;

    protected ObservableList<T> mObservableList;

    protected ChangeObserver mChangeObserver;

    protected DataSetObserver mDataSetObserver;

    public ObservableListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public ObservableListAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ObservableListAdapter(Context context, int resource, ObservableList<T> objects) {
        super(context, resource, objects);
    }

    public ObservableListAdapter(Context context, int resource, int textViewResourceId, ObservableList<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    void init(Context context, ObservableList<T> c) {
        boolean cursorPresent = c != null;
        mObservableList = c;
        mDataValid = cursorPresent;
        mChangeObserver = new ChangeObserver();
        mDataSetObserver = new MyDataSetObserver();

        if (cursorPresent) {
            if (mChangeObserver != null)
                c.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null)
                c.registerDataSetObserver(mDataSetObserver);
        }
    }

    /**
     * Swap in a new ObservableList, returning the old ObservableList.
     * The returned old ObservableList is <em>not</em> closed.
     *
     * @param newObservableList The new observableList to be used.
     * @return Returns the previously set ObservableList, or null if there was a not one.
     * If the given new ObservableList is the same instance is the previously set
     * ObservableList, null is also returned.
     */
    public ObservableList<T> swapObservableList(ObservableList<T> newObservableList) {
        if (newObservableList == mObservableList) {
            return null;
        }
        ObservableList<T> oldObservableList = mObservableList;
        if (oldObservableList != null) {
            if (mChangeObserver != null)
                oldObservableList.unregisterContentObserver(mChangeObserver);
            if (mDataSetObserver != null)
                oldObservableList.unregisterDataSetObserver(mDataSetObserver);
        }
        mObservableList = newObservableList;
        if (newObservableList != null) {
            if (mChangeObserver != null)
                newObservableList.registerContentObserver(mChangeObserver);
            if (mDataSetObserver != null)
                newObservableList.registerDataSetObserver(mDataSetObserver);
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyDataSetInvalidated();
        }
        return oldObservableList;
    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
        }
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
            notifyDataSetInvalidated();
        }
    }
}
