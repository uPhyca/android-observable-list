
package com.uphyca.android.observable;

import android.database.Cursor;

/**
 * Maps cursor columns to a desired element
 * 
 * @param <E> the list element type
 */
public interface Mapper<E> {
    E map(Cursor cursor);
}
