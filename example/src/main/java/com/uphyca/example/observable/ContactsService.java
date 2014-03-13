
package com.uphyca.example.observable;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import com.uphyca.android.observable.ContentProviderObservableListLoader;
import com.uphyca.android.observable.CursorAdapterObservableList;
import com.uphyca.android.observable.ObservableListLoader;

public class ContactsService {

    private final Context mContext;

    public ContactsService(Context context) {
        mContext = context;
    }

    public ObservableListLoader<String> findAll() {
        return new ContactsLoader(mContext);
    }

    private static class ContactsLoader extends ContentProviderObservableListLoader<String> implements CursorAdapterObservableList.Mapper<String> {

        static final String[] PROJECTION = {
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
        };

        public ContactsLoader(Context context) {
            super(context);
            setUri(ContactsContract.Contacts.CONTENT_URI);
            setProjection(PROJECTION);
            setMapper(this);
        }

        @Override
        public String convert(Cursor cursor) {
            return cursor.isNull(0) ? "" : cursor.getString(0);
        }
    }
}
