
package com.uphyca.android.observable;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

public class ICSCursorAdapterObservableListLoader<T> extends ObservableListLoader<T> {

    final ForceLoadContentObserver mObserver;

    Uri mUri;
    String[] mProjection;
    String mSelection;
    String[] mSelectionArgs;
    String mSortOrder;
    CursorAdapterObservableList.Mapper<T> mMapper;

    public ICSCursorAdapterObservableListLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    public ICSCursorAdapterObservableListLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CursorAdapterObservableList.Mapper<T> mapper) {
        super(context);
        mObserver = new ForceLoadContentObserver();
        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
        mMapper = mapper;
    }

    @Override
    public ObservableList<T> loadInBackground() {
        Cursor cursor = getContext().getContentResolver()
                                    .query(mUri, mProjection, mSelection, mSelectionArgs, mSortOrder);
        if (cursor != null) {
            try {
                // Ensure the cursor window is filled.
                cursor.getCount();
                registerContentObserver(cursor, mObserver);
            } catch (RuntimeException ex) {
                cursor.close();
                throw ex;
            }
            return new CursorAdapterObservableList<T>(cursor, mMapper);
        }
        return null;
    }

    void registerContentObserver(Cursor cursor, ContentObserver observer) {
        cursor.registerContentObserver(mObserver);
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

    public CursorAdapterObservableList.Mapper<T> getMapper() {
        return mMapper;
    }

    public void setMapper(CursorAdapterObservableList.Mapper<T> mapper) {
        mMapper = mapper;
    }
}
