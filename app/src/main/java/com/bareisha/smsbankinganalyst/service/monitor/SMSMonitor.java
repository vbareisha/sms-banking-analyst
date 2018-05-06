package com.bareisha.smsbankinganalyst.service.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.preference.PreferenceManager;
import android.telephony.SmsMessage;

import com.bareisha.smsbankinganalyst.R;
import com.bareisha.smsbankinganalyst.service.SmsAppService;
import com.bareisha.smsbankinganalyst.service.SmsLoadingService;
import com.bareisha.smsbankinganalyst.service.api.ISmsAppService;

import java.util.Objects;

/**
 * Created by Vova on 01.02.2018.
 */

public class SMSMonitor extends BroadcastReceiver {

    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && ACTION.compareToIgnoreCase(intent.getAction()) == 0) {
            Object[] pduArray = (Object[]) Objects.requireNonNull(intent.getExtras()).get("pdus");
            if (pduArray != null && pduArray.length > 0) {
                SmsMessage[] messages = new SmsMessage[pduArray.length];
                for (int i = 0; i < pduArray.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
                }
                String sms_from = messages[0].getDisplayOriginatingAddress();
                if (sms_from.equalsIgnoreCase(context.getString(R.string.person_who_send_sms))) {
                    StringBuilder bodyText = new StringBuilder();
                    for (SmsMessage message : messages) {
                        bodyText.append(message.getMessageBody());
                    }
                    String body = bodyText.toString();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

                    String account = sharedPreferences.getString(context.getString(R.string.account_number_key), context.getString(R.string.account_number_default));
                    String cardNumber = sharedPreferences.getString(context.getString(R.string.card_number_key), context.getString(R.string.card_number_default));

                    if (body.indexOf(account) > 0 || body.indexOf(cardNumber) > 0 || account.equals(context.getString(R.string.account_number_default))) {
                        Intent smsLoadingService = new Intent(context, SmsLoadingService.class);
                        smsLoadingService.setAction(ACTION);
                        smsLoadingService.putExtra(context.getString(R.string.sms_body), body);
                        ISmsAppService smsAppService = new SmsAppService();
                        Cursor lastSmsList = smsAppService.getSmsFromAppByBank(context);
                        int lastSmsId = 0;
                        if (lastSmsList != null && lastSmsList.getCount() > 0 && lastSmsList.moveToLast()) {
                            lastSmsId = lastSmsList.getInt(lastSmsList.getColumnIndex("_id"));
                        }
                        smsLoadingService.putExtra(context.getString(R.string.sms_id_app), lastSmsId);

                        context.startService(smsLoadingService);

                        if (sharedPreferences.getBoolean(context.getString(R.string.pref_delete_sms_key), false)) abortBroadcast();
                    }
                }
            }
        }
    }
}
