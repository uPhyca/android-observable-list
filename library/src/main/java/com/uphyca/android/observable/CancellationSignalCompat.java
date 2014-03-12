
package com.uphyca.android.observable;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.CancellationSignal;

class CancellationSignalCompat {

    private final Canceller mCanceller;

    CancellationSignalCompat() {
        mCanceller = getCanceller();
    }

    void cancel() {
        mCanceller.cancel();
    }

    Object getUnderlyingObject() {
        return mCanceller.getUnderlyingObject();
    }

    private static Canceller getCanceller() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN ? new Execution() : new NoOp();
    }

    private interface Canceller {
        void cancel();

        Object getUnderlyingObject();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static class Execution implements Canceller {

        private final CancellationSignal mCancellationSignal = new CancellationSignal();

        @Override
        public void cancel() {
            mCancellationSignal.cancel();
        }

        @Override
        public Object getUnderlyingObject() {
            return mCancellationSignal;
        }
    }

    private static class NoOp implements Canceller {

        @Override
        public void cancel() {
            // no-op
        }

        @Override
        public Object getUnderlyingObject() {
            return null;
        }
    }
}
