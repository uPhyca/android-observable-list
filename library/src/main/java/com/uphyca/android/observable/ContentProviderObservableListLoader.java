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
import android.database.Cursor;
import android.net.Uri;

/**
 * A loader that queries the {@link android.content.ContentResolver} and returns a cursor backed {@link com.uphyca.android.observable.ObservableList}.
 *
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class ContentProviderObservableListLoader<T> extends ObservableListLoader<T> {

    final ForceLoadContentObserver mObserver;

    Uri mUri;
    String[] mProjection;
    String mSelection;
    String[] mSelectionArgs;
    String mSortOrder;
    Mapper<T> mMapper;

    CancellationSignalCompat mCancellationSignal;

    /**
     * Creates an empty unspecified ObservableListLoader.  You must follow this with calls to {@link #setUri(Uri)}, {@link #setSelection(String)}, etc to specify the query to perform.
     */
    public ContentProviderObservableListLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    /**
     * Creates a fully-specified ObservableListLoader.  See
     * {@link android.content.ContentResolver#query(Uri, String[], String, String[], String)} for documentation on the meaning of the parameters.
     * These will be passed as-is to that call.
     */
    public ContentProviderObservableListLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, Mapper<T> mapper) {
        super(context);
        mObserver = new ForceLoadContentObserver();
        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
        mMapper = mapper;
    }

    /* Runs on a worker thread */
    @Override
    public ObservableList<T> loadInBackground() {
        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw OperationCanceledExceptionCompat.create();
            }
            mCancellationSignal = new CancellationSignalCompat();
        }
        try {
            Cursor cursor = QueryCompat.execute(getContext().getContentResolver(), mUri, mProjection, mSelection, mSelectionArgs, mSortOrder, mCancellationSignal);
            if (cursor != null) {
                try {
                    // Ensure the cursor window is filled.
                    cursor.getCount();
                    cursor.registerContentObserver(mObserver);
                } catch (RuntimeException ex) {
                    cursor.close();
                    throw ex;
                }
                return new CursorAdapterObservableList<T>(cursor, mMapper);
            }
            return null;
        } finally {
            synchronized (this) {
                mCancellationSignal = null;
            }
        }
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();

        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public String[] getProjection() {
        return mProjection;
    }

    public void setProjection(String[] projection) {
        mProjection = projection;
    }

    public String getSelection() {
        return mSelection;
    }

    public void setSelection(String selection) {
        mSelection = selection;
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs;
    }

    public void setSelectionArgs(String[] selectionArgs) {
        mSelectionArgs = selectionArgs;
    }

    public String getSortOrder() {
        return mSortOrder;
    }

    public void setSortOrder(String sortOrder) {
        mSortOrder = sortOrder;
    }

    public Mapper<T> getMapper() {
        return mMapper;
    }

    public void setMapper(Mapper<T> mapper) {
        mMapper = mapper;
    }
}
