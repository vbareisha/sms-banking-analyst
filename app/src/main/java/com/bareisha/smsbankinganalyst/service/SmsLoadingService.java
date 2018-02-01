package com.bareisha.smsbankinganalyst.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.bareisha.smsbankinganalyst.service.api.ISmsLoadingService;

/**
 * Created by Vova on 01.02.2018.
 */

public class SmsLoadingService extends IntentService implements ISmsLoadingService {

    public SmsLoadingService() {
        super("SmsLoading");
    }

    @Override
    public void loadSms(String body) {
        System.out.println("get");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        System.out.println(intent.getStringExtra("sms_body"));
        System.out.println("handle");
    }
}
