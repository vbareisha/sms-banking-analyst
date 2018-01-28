package com.bareisha.smsbankinganalyst.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.bareisha.smsbankinganalyst.model.contract.SmsContract.*;

/**
 * Created by Vova on 27.01.2018.
 */

public class SmsDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "sms.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;

    public SmsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE " + SmsEntry.TABLE_NAME + " (" +
                                    SmsEntry._ID    + " INTEGER PRIMARY KEY, " +
                                    SmsEntry.COLUMN_CONSUMPTION + " MONEY NOT NULL, " +
                                    SmsEntry.COLUMN_REST + " MONEY NOT NULL, " +
                                    SmsEntry.COLUMN_DATETIME + " DATETIME NOT NULL, " +
                                    SmsEntry.COLUMN_CURRENCYTYPE + " TEXT NOT NULL, " +
                                    SmsEntry.COLUMN_OPERATIONCURRENCY + " TEXT NOT NULL, " +
                                    SmsEntry.COLUMN_OPERATION + " TEXT NOT NULL, " +
                                    SmsEntry.COLUMN_ORIGINALTEXT + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //stub
    }
}
