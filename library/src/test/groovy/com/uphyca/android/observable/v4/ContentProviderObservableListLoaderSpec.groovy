package com.uphyca.android.observable.v4

import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.support.v4.content.Loader
import com.uphyca.android.observable.Mapper
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowContentResolver2
import org.robolectric.shadows.ShadowSupportAsyncTaskLoader
import org.robolectric.tester.android.database.SimpleTestCursor
import pl.polidea.robospock.RoboSpecification
import spock.util.concurrent.BlockingVariable

import static org.robolectric.Robolectric.application
import static org.robolectric.Robolectric.shadowOf

@Config(manifest = Config.NONE, shadows = [ShadowSupportAsyncTaskLoader, ShadowContentResolver2])
class ContentProviderObservableListLoaderSpec extends RoboSpecification {

    def setupSpec() {
        Robolectric.Reflection.setFinalStaticField(Build.VERSION.class, "SDK_INT", Build.VERSION_CODES.JELLY_BEAN)
    }

    def "startLoading"() {
        given:
        def holder = new BlockingVariable<com.uphyca.android.observable.ObservableList<String>>(1)
        def underTest = new ContentProviderObservableListLoader<String>(application)
        underTest.setMapper(new Mapper<String>() {
            @Override
            String map(Cursor cursor) {
                return cursor.getString(0)
            }
        })
        underTest.setUri(Uri.parse("test"))
        underTest.registerListener(0, { loader, r -> holder.set(r) } as Loader.OnLoadCompleteListener)

        def resolver = application.getContentResolver();
        def shadowResolver = shadowOf(resolver)
        def cursor = new SimpleTestCursor() {
            @Override
            int getCount() { return 1 }

            @Override
            boolean moveToPosition(int position) { return super.moveToNext() }

            @Override
            void registerContentObserver(ContentObserver observer) {}

            @Override
            void unregisterContentObserver(ContentObserver observer) {}
        }
        cursor.setResults([["Bob"]] as Object[][])
        shadowResolver.setCursor(Uri.parse("test"), cursor)

        when:
        underTest.startLoading();

        then:
        holder.get().get(0) == "Bob"
    }
}
