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

import android.app.Activity
import android.database.MatrixCursor
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import pl.polidea.robospock.RoboSpecification

@Config(manifest = Config.NONE)
class ObservableListAdapterSpec extends RoboSpecification {

    def "getItem"() {
        given:
        def underlyingCursor = new MatrixCursor(["name"] as String[])
        def observableList = new CursorAdapterObservableList(underlyingCursor, {
            return it.getString(0)
        } as CursorAdapterObservableList.Mapper)
        underlyingCursor.addRow(["Bob"])
        def activity = Robolectric.buildActivity(Activity).create().get()
        def underTest = new ObservableListAdapter(activity, android.R.layout.simple_expandable_list_item_1, observableList)

        when:
        def actualItem = underTest.getItem(0)

        then:
        actualItem == "Bob"
    }

    def "getItemId"() {
        given:
        def underlyingCursor = new MatrixCursor(["name"] as String[])
        def observableList = new CursorAdapterObservableList(underlyingCursor, {
            return it.getString(0)
        } as CursorAdapterObservableList.Mapper)
        underlyingCursor.addRow(["Bob"])
        def activity = Robolectric.buildActivity(Activity).create().get()
        def underTest = new ObservableListAdapter(activity, android.R.layout.simple_expandable_list_item_1, observableList)

        when:
        def actualItemId = underTest.getItemId(0)

        then:
        actualItemId == 0
    }
}