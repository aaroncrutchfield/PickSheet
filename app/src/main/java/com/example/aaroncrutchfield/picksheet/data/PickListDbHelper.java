package com.example.aaroncrutchfield.picksheet.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Db Helper class for the pick list database. Creates the table for the first time and drops it if
 * the version changes
 */

public class PickListDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "picklist";

    public PickListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LOG_TABLE = "CREATE TABLE " + PickListDbContract.PickListEntry.PICK_LIST_TABLE + " (" +
                PickListDbContract.PickListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PickListDbContract.PickListEntry.COLUMN_PARTNUMBER +  " TEXT, " +                   // TODO: 10/4/17 Make PickListEntry.COLUMN_PARTNUMBER NOT NULL
                PickListDbContract.PickListEntry.COLUMN_CONTAINER_QUANTITY + " INTEGER, " +
                PickListDbContract.PickListEntry.COLUMN_PACK_QUANTITY + " INTEGER, " +
                PickListDbContract.PickListEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PickListDbContract.PickListEntry.PICK_LIST_TABLE);
        onCreate(sqLiteDatabase);
    }
}
