package com.bareisha.smsbankinganalyst;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bareisha.smsbankinganalyst.model.contract.SmsContract;
import com.bareisha.smsbankinganalyst.repository.SmsCursorAdapter;
import com.bareisha.smsbankinganalyst.service.api.IItemOnClickHandler;

/**
 * Created by Vova on 29.01.2018.
 */

public class SmsListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, IItemOnClickHandler {

    private SmsCursorAdapter mAdapter;
    private static final String TAG = SmsListActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_list);

        RecyclerView mRecyclerView = findViewById(R.id.recyclerViewSms);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SmsCursorAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return LoaderFactory.create(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(Long id) {
        Intent smsDetail = new Intent(SmsListActivity.this, SmsDetailActivity.class);
        Uri smsId = SmsContract.SmsEntry.buildSmsUriWithId(id);
        smsDetail.setData(smsId);
        startActivity(smsDetail);
    }

    private static class LoaderFactory {
        static Loader<Cursor> create(final Context context) {
            return new AsyncTaskLoader<Cursor>(context) {

                Cursor mTaskData = null;

                @Override
                protected void onStartLoading() {
                    if (mTaskData != null) {
                        // Delivers any previously loaded data immediately
                        deliverResult(mTaskData);
                    } else {
                        // Force a new load
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {
                    try {
                        return context.getContentResolver().query(SmsContract.SmsEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                SmsContract.SmsEntry._ID);

                    } catch (Exception e) {
                        Log.e(TAG, "Failed to asynchronously load data." + e.getMessage());
                        return null;
                    }
                }

                @Override
                public void deliverResult(Cursor data) {
                    mTaskData = data;
                    super.deliverResult(data);
                }
            };
        }
    }
}