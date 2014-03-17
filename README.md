[![Build Status](https://travis-ci.org/uPhyca/android-observable-list.png?branch=master)](http://travis-ci.org/uPhyca/android-observable-list)


android-observable-list provides an implementation of ObservableList.


Example
----

```Java
class ContactsLoader extends ContentProviderObservableListLoader<String> implements Mapper<String> {

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
```


```Java
public class ContactListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<ObservableList<String>> {

    ObservableListAdapter<String> mList;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(mList = new ObservableListAdapter<String>(getActivity(), android.R.layout.simple_list_item_1));
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ObservableList<String>> onCreateLoader(int id, Bundle args) {
        return new ContactsLoader(getActivity());
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
```



License
-------

    Copyright 2014 uPhyca, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
