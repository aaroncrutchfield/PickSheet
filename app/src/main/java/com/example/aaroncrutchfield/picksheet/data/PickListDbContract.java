package com.example.aaroncrutchfield.picksheet.data;

import android.provider.BaseColumns;

/**
 * Contract file for the pick list database
 */

public class PickListDbContract {
    public static class PickListEntry implements BaseColumns{
        public static final String PICK_LIST_TABLE = "pick_list_table";
        public static final String COLUMN_PARTNUMBER = "partnumber";
        public static final String COLUMN_CONTAINER_QUANTITY = "container_quanitity";
        public static final String COLUMN_PACK_QUANTITY = "pack_quantity";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
