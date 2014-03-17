package com.uphyca.android.observable.v4

import android.os.Build
import android.support.v4.content.Loader
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSupportAsyncTaskLoader
import pl.polidea.robospock.RoboSpecification
import spock.util.concurrent.BlockingVariable

@Config(manifest = Config.NONE, shadows = [ShadowSupportAsyncTaskLoader])
class ObservableListLoaderSpec extends RoboSpecification {

    def setupSpec() {
        Robolectric.Reflection.setFinalStaticField(Build.VERSION.class, "SDK_INT", Build.VERSION_CODES.JELLY_BEAN)
    }

    def "startLoading"() {
        given:
        def holder = new BlockingVariable(1)
        def result = Mock(com.uphyca.android.observable.ObservableList)
        def underTest = new ObservableListLoader<String>(Robolectric.application) {
            @Override
            com.uphyca.android.observable.ObservableList<String> loadInBackground() {
                return result
            }
        }
        underTest.registerListener(0, { loader, r -> holder.set(r) } as Loader.OnLoadCompleteListener)

        when:
        underTest.startLoading();

        then:
        holder.get() == result
    }
}
