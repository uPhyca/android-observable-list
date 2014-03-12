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
import android.os.Build;
import android.os.OperationCanceledException;

/**
 * The adapter of OperationCanceledException to provides transparent operations on supported Android version and not.
 *
 * @author Sosuke Masui (masui@uphyca.com)
 */
class OperationCanceledExceptionCompat {

    static final RuntimeException create() {
        return getFactory().create();
    }

    private static final Factory getFactory() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? new OperationCanceledExceptionFactory() : new IllegalStateExceptionFactory();
    }

    private interface Factory {
        RuntimeException create();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static class OperationCanceledExceptionFactory implements Factory {
        @Override
        public RuntimeException create() {
            return new OperationCanceledException();
        }
    }

    private static class IllegalStateExceptionFactory implements Factory {
        @Override
        public RuntimeException create() {
            return new IllegalStateException();
        }
    }
}
