
package com.uphyca.android.observable;

import android.content.AsyncTaskLoader;
import android.content.Context;

public abstract class ObservableListLoader<T> extends AsyncTaskLoader<ObservableList<T>> {

    ObservableList<T> mObservableList;

    public ObservableListLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(ObservableList<T> observableList) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (observableList != null) {
                observableList.close();
            }
            return;
        }
        ObservableList<T> oldObservableList = mObservableList;
        mObservableList = observableList;

        if (isStarted()) {
            super.deliverResult(observableList);
        }

        if (oldObservableList != null && oldObservableList != observableList && !oldObservableList.isClosed()) {
            oldObservableList.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if (mObservableList != null) {
            deliverResult(mObservableList);
        }
        if (takeContentChanged() || mObservableList == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(ObservableList<T> observableList) {
        if (observableList != null && !observableList.isClosed()) {
            observableList.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (mObservableList != null && !mObservableList.isClosed()) {
            mObservableList.close();
        }
        mObservableList = null;
    }
}
