
package com.uphyca.android.observable;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.OperationCanceledException;

class OperationCanceledExceptionCompat {

    private static final Factory FACTORY;
    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            FACTORY = new OperationCanceledExceptionFactory();
        } else {
            FACTORY = new IllegalStateExceptionFactory();
        }
    }

    static final RuntimeException create() {
        return FACTORY.create();
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
