package com.bareisha.smsbankinganalyst.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.bareisha.smsbankinganalyst.model.contract.SmsContract;
import com.bareisha.smsbankinganalyst.service.api.ISmsLoadingService;
import com.vbareisha.parser.core.dto.SMSDto;
import com.vbareisha.parser.core.enums.ParserType;
import com.vbareisha.parser.service.Parser;

import java.util.Date;

/**
 * Created by Vova on 01.02.2018.
 */

public class SmsLoadingService extends IntentService implements ISmsLoadingService {

    public SmsLoadingService() {
        super("SmsLoading");
    }

    @Override
    public void loadSms(String body) {
        Parser parser = new Parser();
        SMSDto sms = parser.getSumFromText(body, ParserType.SMS);
        // Insert into db
        ContentValues contentValues = new ContentValues();
        contentValues.put(SmsContract.SmsEntry.COLUMN_CONSUMPTION, sms.getConsumption().doubleValue());
        contentValues.put(SmsContract.SmsEntry.COLUMN_CURRENCYTYPE, sms.getCurrencyType().name());
        //todo convert locadatetime to date
        Date t = new Date();
        contentValues.put(SmsContract.SmsEntry.COLUMN_DATETIME, t.toString());
        contentValues.put(SmsContract.SmsEntry.COLUMN_OPERATION, sms.getOperation().name());
        contentValues.put(SmsContract.SmsEntry.COLUMN_OPERATIONCURRENCY, sms.getOperationCurrency().name());
        contentValues.put(SmsContract.SmsEntry.COLUMN_ORIGINALTEXT, sms.getOriginalText());
        contentValues.put(SmsContract.SmsEntry.COLUMN_REST, sms.getRest().doubleValue());
        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(SmsContract.SmsEntry.CONTENT_URI, contentValues);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) loadSms(intent.getStringExtra("sms_body"));
    }
}
