package com.bareisha.smsbankinganalyst.model.contract;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Vova on 27.01.2018.
 */

public class SmsContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.bareisha.smsbankinganalyst";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_TASKS = "sms";

    public static final class SmsEntry implements BaseColumns {
        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        // Sms table and column names
        public static final String TABLE_NAME = "sms";

        // Since SmsEntry implements the interface "BaseColumns", it has an automatically produced "_ID" column
        public static final String COLUMN_CONSUMPTION = "consumption";
        public static final String COLUMN_REST = "rest";
        public static final String COLUMN_DATETIME = "date_time";
        public static final String COLUMN_CURRENCYTYPE = "currency_type";
        public static final String COLUMN_OPERATIONCURRENCY = "operation_currency";
        public static final String COLUMN_OPERATION = "operation";
        public static final String COLUMN_ORIGINALTEXT = "original_text";
    }
}
