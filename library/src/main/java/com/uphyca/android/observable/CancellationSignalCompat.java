
package com.uphyca.android.observable;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.CancellationSignal;

class CancellationSignalCompat {

    private static final Allocator ALLOCATOR;
    private static final Canceller CANCELLER;
    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ALLOCATOR = new Allocation();
            CANCELLER = new Execution();
        } else {
            ALLOCATOR = new Null();
            CANCELLER = new NoOp();
        }
    }

    private final Object mUnderlyingObject;

    CancellationSignalCompat() {
        mUnderlyingObject = ALLOCATOR.allocate();
    }

    void cancel() {
        CANCELLER.cancel(mUnderlyingObject);
    }

    Object getUnderlyingObject() {
        return mUnderlyingObject;
    }

    private interface Allocator {
        Object allocate();
    }

    private static class Allocation implements Allocator {
        @Override
        public Object allocate() {
            return new CancellationSignal();
        }
    }

    private static class Null implements Allocator {
        @Override
        public Object allocate() {
            return null;
        }
    }

    private interface Canceller {
        void cancel(Object cancellationSignal);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static class Execution implements Canceller {
        @Override
        public void cancel(Object cancellationSignal) {
            CancellationSignal.class.cast(cancellationSignal)
                                    .cancel();
        }
    }

    private static class NoOp implements Canceller {
        @Override
        public void cancel(Object cancellationSignal) {
            // no-op
        }
    }
}
