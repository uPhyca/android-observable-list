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

package com.uphyca.example.observable.v4;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.uphyca.android.observable.ObservableList;
import com.uphyca.android.observable.ObservableListAdapter;
import com.uphyca.example.observable.v4.R;

public class MainActivity extends FragmentActivity {

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
            return new ContactsService(getActivity()).findAll();
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
