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

package com.uphyca.android.observable.v4;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.uphyca.android.observable.CursorAdapterObservableList;
import com.uphyca.android.observable.Mapper;
import com.uphyca.android.observable.ObservableList;

/**
 * A loader that queries the {@link android.database.sqlite.SQLiteDatabase} and returns a cursor backed {@link com.uphyca.android.observable.ObservableList}.
 * 
 * @author Sosuke Masui (masui@uphyca.com)
 */
public class SQLiteObservableListLoader<T> extends ObservableListLoader<T> {

    final ForceLoadContentObserver mObserver;

    SQLiteOpenHelper mSQLiteOpenHelper;
    boolean mDistinct;
    String mTable;
    String[] mColumns;
    String mSelection;
    String[] mSelectionArgs;
    String mGroupBy;
    String mHaving;
    String mOrderBy;
    String mLimit;

    SQLiteDatabase mSQLiteDatabase;

    Mapper<T> mMapper;

    /**
     * Creates an empty unspecified ObservableListLoader. You must follow this with calls to {@link #setTable(String)}, {@link #setSelection(String)}, etc to specify the query to perform.
     */
    public SQLiteObservableListLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    /**
     * Creates a fully-specified CursorLoader. See {@link android.database.sqlite.SQLiteDatabase#query(boolean, String, String[], String, String[], String, String, String, String)} for documentation on the meaning of the parameters.
     * These will be passed as-is to that call.
     */
    public SQLiteObservableListLoader(Context context, SQLiteOpenHelper SQLiteOpenHelper, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, Mapper<T> mapper) {
        super(context);
        mObserver = new ForceLoadContentObserver();
        mSQLiteOpenHelper = SQLiteOpenHelper;
        mDistinct = distinct;
        mTable = table;
        mColumns = columns;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = orderBy;
        mLimit = limit;
        mMapper = mapper;
    }

    /* Runs on a worker thread */
    @Override
    public ObservableList<T> loadInBackground() {
        mSQLiteDatabase = mSQLiteOpenHelper.getReadableDatabase();
        if (mSQLiteDatabase == null) {
            return null;
        }
        Cursor cursor = mSQLiteDatabase.query(mDistinct, mTable, mColumns, mSelection, mSelectionArgs, mGroupBy, mHaving, mOrderBy, mLimit);
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
    }

    @Override
    public void onCanceled(ObservableList<T> observableList) {
        super.onCanceled(observableList);
        if (mSQLiteDatabase != null && mSQLiteDatabase.isOpen()) {
            mSQLiteDatabase.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        if (mSQLiteDatabase != null && mSQLiteDatabase.isOpen()) {
            mSQLiteDatabase.close();
        }
        mSQLiteDatabase = null;
    }

    public SQLiteOpenHelper getSQLiteOpenHelper() {
        return mSQLiteOpenHelper;
    }

    public void setSQLiteOpenHelper(SQLiteOpenHelper SQLiteOpenHelper) {
        mSQLiteOpenHelper = SQLiteOpenHelper;
    }

    public boolean isDistinct() {
        return mDistinct;
    }

    public void setDistinct(boolean distinct) {
        mDistinct = distinct;
    }

    public String getTable() {
        return mTable;
    }

    public void setTable(String table) {
        mTable = table;
    }

    public String[] getColumns() {
        return mColumns;
    }

    public void setColumns(String[] columns) {
        mColumns = columns;
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

    public String getGroupBy() {
        return mGroupBy;
    }

    public void setGroupBy(String groupBy) {
        mGroupBy = groupBy;
    }

    public String getHaving() {
        return mHaving;
    }

    public void setHaving(String having) {
        mHaving = having;
    }

    public String getOrderBy() {
        return mOrderBy;
    }

    public void setOrderBy(String orderBy) {
        mOrderBy = orderBy;
    }

    public String getLimit() {
        return mLimit;
    }

    public void setLimit(String limit) {
        mLimit = limit;
    }

    public Mapper<T> getMapper() {
        return mMapper;
    }

    public void setMapper(Mapper<T> mapper) {
        mMapper = mapper;
    }
}
