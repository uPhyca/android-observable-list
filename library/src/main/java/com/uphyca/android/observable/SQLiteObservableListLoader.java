
package com.uphyca.android.observable;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    CursorAdapterObservableList.Mapper<T> mMapper;
    CancellationSignalCompat mCancellationSignal;

    public SQLiteObservableListLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    public SQLiteObservableListLoader(Context context, SQLiteOpenHelper SQLiteOpenHelper, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CursorAdapterObservableList.Mapper<T> mapper) {
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

    @Override
    public ObservableList<T> loadInBackground() {
        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw OperationCanceledExceptionCompat.create();
            }
            mCancellationSignal = new CancellationSignalCompat();
        }
        try {
            mSQLiteDatabase = mSQLiteOpenHelper.getReadableDatabase();
            if (mSQLiteDatabase == null) {
                return null;
            }
            Cursor cursor = QueryCompat.execute(mSQLiteDatabase, mDistinct, mTable, mColumns, mSelection, mSelectionArgs, mGroupBy, mHaving, mOrderBy, mLimit, mCancellationSignal);
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

    public CursorAdapterObservableList.Mapper<T> getMapper() {
        return mMapper;
    }

    public void setMapper(CursorAdapterObservableList.Mapper<T> mapper) {
        mMapper = mapper;
    }
}
