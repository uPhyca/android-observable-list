package org.robolectric.shadows;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(ContentResolver.class)
public class ShadowContentResolver2 extends ShadowContentResolver {

    @Implementation
    public final Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        return query(uri, projection, selection, selectionArgs, sortOrder);
    }
}
