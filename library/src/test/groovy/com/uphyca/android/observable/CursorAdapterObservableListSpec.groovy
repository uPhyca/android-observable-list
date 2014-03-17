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

package com.uphyca.android.observable

import android.database.DataSetObserver
import android.database.MatrixCursor
import android.os.Build
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import pl.polidea.robospock.RoboSpecification

@Config(manifest = Config.NONE)
class CursorAdapterObservableListSpec extends RoboSpecification {

    def setupSpec() {
        Robolectric.Reflection.setFinalStaticField(Build.VERSION.class, "SDK_INT", Build.VERSION_CODES.JELLY_BEAN)
    }

    def "get"() {
        given:
        def underlyingCursor = new MatrixCursor(["name"] as String[])
        def underTest = new CursorAdapterObservableList(underlyingCursor, {
            return it.getString(0)
        } as Mapper)
        underlyingCursor.addRow(["Bob"])

        when:
        def actualObject = underTest.get(0)

        then:
        !underTest.isEmpty()
        underTest.size() == 1
        actualObject == "Bob"
    }

    def "add is not supported"() {
        given:
        def underlyingCursor = new MatrixCursor(["name"] as String[])
        def underTest = new CursorAdapterObservableList(underlyingCursor, {
            return it.getString(0)
        } as Mapper)

        when:
        underTest.add("Bob")

        then:
        thrown(UnsupportedOperationException)
        underTest.isEmpty()
    }

    def "remove is not supported"() {
        given:
        def underlyingCursor = new MatrixCursor(["name"] as String[])
        def underTest = new CursorAdapterObservableList(underlyingCursor, {
            return it.getString(0)
        } as Mapper)
        underlyingCursor.addRow(["Bob"])

        when:
        underTest.remove("Bob")

        then:
        thrown(UnsupportedOperationException)
        underTest.size() == 1
    }

    def "indexOf"() {
        given:
        def underlyingCursor = new MatrixCursor(["name"] as String[])
        def underTest = new CursorAdapterObservableList(underlyingCursor, {
            return it.getString(0)
        } as Mapper)
        underlyingCursor.addRow(["Bob"])

        when:
        def actualIndex = underTest.indexOf("Bob")

        then:
        actualIndex == 0
    }

    def "index out of bounds"() {
        given:
        def underlyingCursor = new MatrixCursor(["name"] as String[])
        def underTest = new CursorAdapterObservableList(underlyingCursor, {
            return it.getString(0)
        } as Mapper)

        when:
        underTest.get(0)

        then:
        thrown(IndexOutOfBoundsException)
    }

    def "notify dataSet changes"() {
        given:
        def underlyingCursor = new MatrixCursor(["name"] as String[])
        def underTest = new CursorAdapterObservableList(underlyingCursor, {
            return it.getString(0)
        } as Mapper)
        def dataSetObserver = Mock(DataSetObserver)
        underTest.registerDataSetObserver(dataSetObserver)

        when:
        underlyingCursor.requery()

        then:
        1 * dataSetObserver.onChanged()
    }

    def "close"() {
        given:
        def underlyingCursor = new MatrixCursor(["name"] as String[])
        def underTest = new CursorAdapterObservableList(underlyingCursor, {
            return it.getString(0)
        } as Mapper)

        when:
        underTest.close()

        then:
        underTest.isClosed()
        underlyingCursor.isClosed()
    }

    def "iterator"() {
        given:
        def underlyingCursor = new MatrixCursor(["name"] as String[])
        def underTest = new CursorAdapterObservableList(underlyingCursor, {
            return it.getString(0)
        } as Mapper)

        underlyingCursor.addRow(["Bob"])

        when:
        def iterator = underTest.iterator()

        then:
        iterator.hasNext()
        iterator.next() == "Bob"
        !iterator.hasNext()
    }
}
