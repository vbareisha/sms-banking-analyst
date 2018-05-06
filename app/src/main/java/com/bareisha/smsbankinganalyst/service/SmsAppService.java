package com.bareisha.smsbankinganalyst.service;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.bareisha.smsbankinganalyst.R;
import com.bareisha.smsbankinganalyst.model.contract.SmsContract;
import com.bareisha.smsbankinganalyst.service.api.ISmsAppService;

public class SmsAppService implements ISmsAppService {
    @Override
    public Cursor getSmsFromAppByBank(Context context) {
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

    private static int getLastSmsId(int lastSmsId, Cursor cursor) {
        String valueMaxId = cursor.getString(cursor.getColumnIndex("maxId"));
        if (valueMaxId != null && !valueMaxId.equals("null")) {
            lastSmsId = Integer.parseInt(valueMaxId);
        }
        return lastSmsId;
    }
}
