package com.bareisha.smsbankinganalyst.service.api;

import android.content.Context;
import android.database.Cursor;

public interface ISmsAppService {
    Cursor getSmsFromAppByBank(Context context);
}
