
package com.uphyca.android.observable;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.OperationCanceledException;

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
