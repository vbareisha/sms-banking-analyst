package com.bareisha.smsbankinganalyst.service.api;

import android.content.ContentResolver;

/**
 * Created by Vova on 01.02.2018.
 */

public interface ISmsLoadingService {
    void loadSms(String body, int smsIdApp, ContentResolver contentResolver);
}
