
package com.uphyca.android.observable;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Build;

public abstract class ObservableListLoader<T> extends AsyncTaskLoader<ObservableList<T>> {

    ObservableList<T> mObservableList;

    public ObservableListLoader(Context context) {
        super(context);
    }

    @Override
    public void deliverResult(ObservableList<T> observableList) {
        if (isReset()) {
            // An async execute came in while the loader is stopped
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

    private static final LoadInBackgroundCanceled getLoadInBackgroundCanceled() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? new LoadInBackgroundCanceledDelegate() : new UnsupportedLoadInBackgroundCanceled();
    }

    @Override
    public boolean isLoadInBackgroundCanceled() {
        return getLoadInBackgroundCanceled().get(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private final boolean callIsLoadInBackgroundCanceled() {
        return super.isLoadInBackgroundCanceled();
    }

    private interface LoadInBackgroundCanceled {
        boolean get(ObservableListLoader<?> owner);
    }

    private static class LoadInBackgroundCanceledDelegate implements LoadInBackgroundCanceled {
        @Override
        public boolean get(ObservableListLoader<?> owner) {
            return owner.callIsLoadInBackgroundCanceled();
        }
    }

    private static class UnsupportedLoadInBackgroundCanceled implements LoadInBackgroundCanceled {
        @Override
        public boolean get(ObservableListLoader<?> owner) {
            return false;
        }
    }
}
