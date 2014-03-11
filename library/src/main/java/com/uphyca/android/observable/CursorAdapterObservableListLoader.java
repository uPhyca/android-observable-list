
package com.uphyca.android.observable;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CursorAdapterObservableListLoader<T> extends ObservableListLoader<T> {

    final ForceLoadContentObserver mObserver;

    Uri mUri;
    String[] mProjection;
    String mSelection;
    String[] mSelectionArgs;
    String mSortOrder;
    CursorAdapterObservableList.Mapper<T> mMapper;

    CancellationSignal mCancellationSignal;

    public CursorAdapterObservableListLoader(Context context) {
        super(context);
        mObserver = new ForceLoadContentObserver();
    }

    public CursorAdapterObservableListLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CursorAdapterObservableList.Mapper<T> mapper) {
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
        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }
            mCancellationSignal = new CancellationSignal();
        }
        try {
            Cursor cursor = getContext().getContentResolver()
                                        .query(mUri, mProjection, mSelection, mSelectionArgs, mSortOrder, mCancellationSignal);
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

    public CursorAdapterObservableList.Mapper<T> getMapper() {
        return mMapper;
    }

    public void setMapper(CursorAdapterObservableList.Mapper<T> mapper) {
        mMapper = mapper;
    }
}
