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

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;

/**
 * The adapter of query method with CancellationSignal to provides transparent operations on supported Android version and not.
 *
 * @author Sosuke Masui (masui@uphyca.com)
 */
class QueryCompat {

    private static Query getQuery() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? new CancellableQuery() : new OutgoingQuery();
    }

    public static Cursor execute(ContentResolver contentResolver, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignalCompat cancellationSignal) {
        return getQuery().execute(contentResolver, uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
    }

    public static Cursor execute(SQLiteDatabase sqLiteDatabase, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignalCompat cancellationSignal) {
        return getQuery().execute(sqLiteDatabase, distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal);
    }

    private interface Query {
        Cursor execute(ContentResolver contentResolver, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignalCompat cancellationSignal);

        Cursor execute(SQLiteDatabase sqLiteDatabase, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignalCompat cancellationSignal);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static class CancellableQuery implements Query {
        @Override
        public Cursor execute(ContentResolver contentResolver, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignalCompat cancellationSignal) {
            return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder, (CancellationSignal) cancellationSignal.getUnderlyingObject());
        }

        @Override
        public Cursor execute(SQLiteDatabase sqLiteDatabase, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignalCompat cancellationSignal) {
            return sqLiteDatabase.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, (CancellationSignal) cancellationSignal.getUnderlyingObject());
        }
    }

    private static class OutgoingQuery implements Query {
        @Override
        public Cursor execute(ContentResolver contentResolver, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignalCompat cancellationSignal) {
            return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
        }

        @Override
        public Cursor execute(SQLiteDatabase sqLiteDatabase, boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignalCompat cancellationSignal) {
            return sqLiteDatabase.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        }
    }
}
