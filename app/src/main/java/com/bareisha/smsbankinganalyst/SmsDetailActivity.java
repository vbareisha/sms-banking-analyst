package com.bareisha.smsbankinganalyst;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bareisha.smsbankinganalyst.model.contract.SmsContract;

/**
 * Created by Vova on 04.02.2018.
 */

public class SmsDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SHARE_HASHTAG = " #SmsBankingAnalyst";
    private static final int ID_DETAIL_LOADER = 353;
    private TextView mDescription;
    private Uri mUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_detail_activity);

        mDescription = findViewById(R.id.description);
        mUri = getIntent().getData();
        if (mUri == null) throw new RuntimeException("Uri for sms detail cannot by null!");

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_DETAIL_LOADER: {
                return new CursorLoader(
                        this,
                        mUri,
                        null,
                        null,
                        null,
                        null);
            }
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }

        mDescription.setText(data.getString(data.getColumnIndex(SmsContract.SmsEntry.COLUMN_ORIGINALTEXT)));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Intent createShareForecastIntent() {
        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mDescription.getText() + SHARE_HASHTAG)
                .getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_sms, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        item.setIntent(createShareForecastIntent());
        return true;
    }
}
