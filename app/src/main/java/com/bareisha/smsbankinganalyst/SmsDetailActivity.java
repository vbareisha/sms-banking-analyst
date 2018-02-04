package com.bareisha.smsbankinganalyst;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by Vova on 04.02.2018.
 */

public class SmsDetailActivity extends AppCompatActivity {

    private TextView mDescription;
    private Uri mUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_detail_activity);

        mDescription = findViewById(R.id.description);
        mUri = getIntent().getData();

        mDescription.setText("DADADADADA");
        System.out.println(mUri);
    }
}
