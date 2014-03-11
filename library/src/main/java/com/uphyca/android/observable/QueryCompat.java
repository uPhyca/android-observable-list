
package com.uphyca.android.observable;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;

class QueryCompat {

    private static final Query QUERY;
    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            QUERY = new CancellableQuery();
        } else {
            QUERY = new OutgoingQuery();
        }
    }

    public static Cursor execute(ContentResolver contentResolver, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignalCompat cancellationSignal) {
        return QUERY.execute(contentResolver, uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    private interface Query {
        Cursor execute(ContentResolver contentResolver, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignalCompat cancellationSignal);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static class CancellableQuery implements Query {
        @Override
        public Cursor execute(ContentResolver contentResolver, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignalCompat cancellationSignal) {
            return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder, (CancellationSignal) cancellationSignal.getUnderlyingObject());
        }
    }

    private static class OutgoingQuery implements Query {
        @Override
        public Cursor execute(ContentResolver contentResolver, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignalCompat cancellationSignal) {
            return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
        }
    }
}
