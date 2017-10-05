package com.example.aaroncrutchfield.picksheet.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * Convenience class that holds methods for interacting with the pick list database
 */

public class PickListDbUtility {

    private static final String TAG = PickListDbUtility.class.getSimpleName();

    /**
     * Convenience method for entering a part into the database
     * @param partnumber the item to be entered
     * @param db a reference to the SQLiteDatabase the item will be entered into
     * @return rowId or -1 if there was an error or if the partnumber already exists
     */
    public static long addDatabaseEntry(String partnumber, SQLiteDatabase db){
        //Check if the part number already exists
        Cursor cursor = null;
        try {
            cursor = db.query(PickListDbContract.PickListEntry.PICK_LIST_TABLE,
                    new String[]{PickListDbContract.PickListEntry.COLUMN_PARTNUMBER},
                    PickListDbContract.PickListEntry.COLUMN_PARTNUMBER +"=?",
                    new String[] {partnumber},
                    PickListDbContract.PickListEntry.COLUMN_PARTNUMBER,
                    null,
                    null);
        } catch (SQLiteException e){

        }
        int count = cursor.getCount();
        cursor.close();

        if (count > 0) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(PickListDbContract.PickListEntry.COLUMN_PARTNUMBER, partnumber);
        long rowId = db.insert(
                PickListDbContract.PickListEntry.PICK_LIST_TABLE,
                null,
                values);

        Log.d(TAG, "addDatabaseEntry: rowId= " + rowId);
        return rowId;
    }

    /**
     * Convenience method for getting a Cursor that only contains data from the part number column.
     * The results will have no duplicates and will be ordered by part number as well.
     * @param db a reference to the SQLiteDatabase to get the entries from
     * @return Cursor containing only the part number column
     */
    public static Cursor getPartnumbersForPickListAdapter(SQLiteDatabase db) {
        Cursor cursorWithEntries = db.query(
                PickListDbContract.PickListEntry.PICK_LIST_TABLE,
                new String[] {PickListDbContract.PickListEntry.COLUMN_PARTNUMBER},
                null,
                null,
                PickListDbContract.PickListEntry.COLUMN_PARTNUMBER,
                null,
                PickListDbContract.PickListEntry.COLUMN_PARTNUMBER);

        return cursorWithEntries;
    }


}
