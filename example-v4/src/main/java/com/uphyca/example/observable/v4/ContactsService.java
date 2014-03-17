
package com.uphyca.example.observable.v4;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import com.uphyca.android.observable.Mapper;
import com.uphyca.android.observable.v4.ContentProviderObservableListLoader;
import com.uphyca.android.observable.v4.ObservableListLoader;

public class ContactsService {

    private final Context mContext;

    public ContactsService(Context context) {
        mContext = context;
    }

    public ObservableListLoader<String> findAll() {
        return new ContactsLoader(mContext);
    }

    private static class ContactsLoader extends ContentProviderObservableListLoader<String> implements Mapper<String> {

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
        public String map(Cursor cursor) {
            return cursor.isNull(0) ? "" : cursor.getString(0);
        }
    }
}
