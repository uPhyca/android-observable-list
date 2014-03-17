/*
 * Copyright (C) 2014 uPhyca Inc.
 *
 * Copyright (C) 2010 The Android Open Source Project
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

package com.uphyca.android.observable.v4;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.uphyca.android.observable.ObservableList;

/**
 * Abstract Loader that provides an ObservableList.
 * 
 * @author Sosuke Masui (masui@uphyca.com)
 */
public abstract class ObservableListLoader<T> extends AsyncTaskLoader<ObservableList<T>> {

    ObservableList<T> mObservableList;

    public ObservableListLoader(Context context) {
        super(context);
    }

    /* Runs on the UI thread */
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
}
