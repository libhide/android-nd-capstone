package com.ratik.todone.provider;

import android.provider.BaseColumns;

/**
 * Created by Ratik on 17/12/16.
 */

public class TodoContract {

    private TodoContract() {}

    public static class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todo";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TASK = "task";
        public static final String COLUMN_CHECKED = "checked";
    }
}
