package com.uphyca.android.observable

import android.content.Loader
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAsyncTaskLoader
import pl.polidea.robospock.RoboSpecification
import spock.util.concurrent.BlockingVariable

@Config(manifest = Config.NONE, shadows = [ShadowAsyncTaskLoader])
class ObservableListLoaderICSSpec extends RoboSpecification {

    def "startLoading"() {
        given:
        def holder = new BlockingVariable(1)
        def result = Mock(ObservableList)
        def underTest = new ObservableListLoader<String>(Robolectric.application) {
            @Override
            ObservableList<String> loadInBackground() {
                return result
            }
        }
        underTest.registerListener(0, { loader, r -> holder.set(r) } as Loader.OnLoadCompleteListener)

        when:
        underTest.startLoading();

        then:
        holder.get() == result
    }

    def "isLoadInBackgroundCanceled"() {
        when:
        def underTest = new ObservableListLoader<String>(Robolectric.application) {
            @Override
            ObservableList<String> loadInBackground() {
                return result
            }
        }
        then:
        !underTest.isLoadInBackgroundCanceled()
    }
}
