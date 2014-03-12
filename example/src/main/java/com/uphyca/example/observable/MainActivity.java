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

package com.uphyca.example.observable;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import com.uphyca.android.observable.ContentProviderObservableListLoader;
import com.uphyca.android.observable.CursorAdapterObservableList;
import com.uphyca.android.observable.ObservableList;
import com.uphyca.android.observable.ObservableListAdapter;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public static class ContactListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<ObservableList<String>> {

        ObservableListAdapter<String> mList;

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(mList = new ObservableListAdapter<String>(getActivity(), android.R.layout.simple_list_item_1));
            getLoaderManager().initLoader(0, null, this);
        }

        @Override
        public Loader<ObservableList<String>> onCreateLoader(int id, Bundle args) {
            String[] projection = {
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
            };
            CursorAdapterObservableList.Mapper<String> mapper = new CursorAdapterObservableList.Mapper<String>() {
                @Override
                public String convert(Cursor cursor) {
                    return !cursor.isNull(0) ? cursor.getString(0) : "<unknown>";
                }
            };
            return new ContentProviderObservableListLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, projection, null, null, null, mapper);
        }

        @Override
        public void onLoadFinished(Loader<ObservableList<String>> loader, ObservableList<String> data) {
            mList.swapObservableList(data);
        }

        @Override
        public void onLoaderReset(Loader<ObservableList<String>> loader) {
            mList.swapObservableList(null);
        }
    }
}
