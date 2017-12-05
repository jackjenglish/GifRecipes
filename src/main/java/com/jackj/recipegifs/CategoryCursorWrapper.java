package com.jackj.recipegifs;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

/**
 * Created by jackj on 03/01/2017.
 */
public class CategoryCursorWrapper extends CursorWrapper {
    public CategoryCursorWrapper(Cursor cursor){
        super(cursor);
    }
    public UUID getUUID(){
        String uuidString = getString(getColumnIndex(SearchCategoryDbSchema.SearchCategoryTable.Cols.UUID));
        UUID id= UUID.fromString(uuidString);
        return id;
    }

}
