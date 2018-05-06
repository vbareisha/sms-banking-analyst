package com.bareisha.smsbankinganalyst.loader;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.bareisha.smsbankinganalyst.R;
import com.bareisha.smsbankinganalyst.core.exceptions.NotSupportedIntent;
import com.bareisha.smsbankinganalyst.model.contract.SmsContract;
import com.bareisha.smsbankinganalyst.service.SmsLoadingService;
import com.bareisha.smsbankinganalyst.service.api.ISmsLoadingService;

public class SmsScanerFromDevice implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ID_SCANER_SMS_FROM_DEVICE_LOADER = 112;

    private Context context;
    private ISmsLoadingService smsLoadingService = new SmsLoadingService();

    public SmsScanerFromDevice(Context context) {
        this.context = context;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return loaderFactory(context);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            while (data.moveToNext()) {
                String body = data.getString(data.getColumnIndex("body"));
                // отсекаем ключи авторизации
                if (body.length() > 4) {
                    smsLoadingService.loadSms(
                            data.getString(data.getColumnIndex("body")),
                            data.getInt(data.getColumnIndex("_id")),
                            loader.getContext().getContentResolver());
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //stub
    }

    private static Loader<Cursor> loaderFactory(final Context context) {

        return new AsyncTaskLoader<Cursor>(context) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                Uri maxIdUri = Uri.parse(SmsContract.SmsEntry.CONTENT_URI + "/max_id");
                int lastSmsId = 0;
                try (Cursor cursor = context.getContentResolver().query(maxIdUri, null, null, null, null)) {
                    if (cursor != null && cursor.move(1)) {
                        lastSmsId = getLastSmsId(lastSmsId, cursor);
                    }
                }
                Cursor result = null;
                if (lastSmsId == 0) {
                    result = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, "address = ?", new String[] {context.getString(R.string.person_who_send_sms)}, "_id");
                } else {
                    Uri smsById = SmsContract.SmsEntry.buildSmsUriWithId(lastSmsId);
                    try (Cursor lastSmsCursor = context.getContentResolver().query(smsById, null, null, null, null)) {
                        if (lastSmsCursor != null && lastSmsCursor.move(1)) {
                            int lastSmsIdApp = lastSmsCursor.getInt(lastSmsCursor.getColumnIndex(SmsContract.SmsEntry.COLUMN_SMS_ID_APP));
                            result = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, "address = ? AND _id > ?", new String[] {context.getString(R.string.person_who_send_sms), String.valueOf(lastSmsIdApp)}, "_id");
                        }
                    }
                }
                return result;
            }
        };
    }

    private static int getLastSmsId(int lastSmsId, Cursor cursor) {
        String valueMaxId = cursor.getString(cursor.getColumnIndex("maxId"));
        if (valueMaxId != null && !valueMaxId.equals("null")) {
            lastSmsId = Integer.parseInt(valueMaxId);
        }
        return lastSmsId;
    }
}
