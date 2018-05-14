package com.bareisha.smsbankinganalyst.loader;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;

import com.bareisha.smsbankinganalyst.R;
import com.bareisha.smsbankinganalyst.service.SmsAppService;
import com.bareisha.smsbankinganalyst.service.SmsLoadingService;
import com.bareisha.smsbankinganalyst.service.api.ISmsAppService;
import com.bareisha.smsbankinganalyst.service.api.ISmsLoadingService;

public class SmsScanerFromDevice implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int ID_SCANER_SMS_FROM_DEVICE_LOADER = 112;

    private Context context;
    private ISmsLoadingService smsLoadingService = new SmsLoadingService();

    public SmsScanerFromDevice(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return loaderFactory(context);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String account = sharedPreferences.getString(context.getString(R.string.account_number_key), context.getString(R.string.account_number_default));
            String cardNumber = sharedPreferences.getString(context.getString(R.string.card_number_key), context.getString(R.string.card_number_default));
            while (data.moveToNext()) {
                String body = data.getString(data.getColumnIndex("body"));
                // отсекаем ключи авторизации
                if (body.indexOf(account) > 0 || body.indexOf(cardNumber) > 0 || account.equals(context.getString(R.string.account_number_default))) {
                    smsLoadingService.loadSms(
                            data.getString(data.getColumnIndex("body")),
                            data.getInt(data.getColumnIndex("_id")),
                            loader.getContext().getContentResolver());
                }
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
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
                ISmsAppService smsAppService = new SmsAppService();
                return smsAppService.getSmsFromAppByBank(context);
            }
        };
    }
}
