package com.bareisha.smsbankinganalyst.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.bareisha.smsbankinganalyst.model.contract.SmsContract;

public class SmsScanerFromDevice implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ID_SCANER_SMS_FROM_DEVICE_LOADER = 112;

    private Context context;

    public SmsScanerFromDevice(Context context) {
        this.context = context;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(context) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                //todo загрузка смс из хранилища
                //Cursor cur = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);
                Uri maxIdUri = Uri.parse(SmsContract.SmsEntry.CONTENT_URI + "/max_id");
                Cursor cursor = context.getContentResolver().query( maxIdUri, null, null, null,null);
                if (cursor != null) {
                    while(cursor.moveToFirst()) {
                        System.out.println(cursor);
                        cursor.moveToNext();
                    }
                }
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        System.out.println("finish!");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //stub
    }
}
