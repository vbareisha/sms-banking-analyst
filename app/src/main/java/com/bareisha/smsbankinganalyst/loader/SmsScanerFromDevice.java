package com.bareisha.smsbankinganalyst.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import com.bareisha.smsbankinganalyst.R;
import com.bareisha.smsbankinganalyst.model.contract.SmsContract;

public class SmsScanerFromDevice implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ID_SCANER_SMS_FROM_DEVICE_LOADER = 112;

    private Context context;

    public SmsScanerFromDevice(Context context) {
        this.context = context;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return loaderFactory(context);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //todo здесь мы загружаем в свою систему информацию
        System.out.println(data);
        data.moveToFirst();
        System.out.println(data.getString(data.getColumnIndex("person")));
        System.out.println("finish!");
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
                //todo построить фильтры, по id и по получателю подключить device
                if (lastSmsId == 0) {
                    //load everything
                    return context.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, "address = ?", new String[] {context.getString(R.string.person_who_send_sms)}, null);
                } else {
                    Uri smsById = SmsContract.SmsEntry.buildSmsUriWithId(lastSmsId);
                    //todo добавить поле которое хранит идентификатор смс в телефоне
                    Cursor sms = context.getContentResolver().query(smsById, null, null, null, null);
                    if (sms != null & sms.moveToFirst()) {
                        //todo извлекаем по идентификатору из телефона
                        return context.getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);
                    }
                    return null;
                }
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
