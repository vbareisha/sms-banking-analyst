package com.bareisha.smsbankinganalyst;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bareisha.smsbankinganalyst.model.contract.SmsContract;
import com.vbareisha.parser.core.enums.CurrencyType;

import static android.content.SharedPreferences.*;

public class MainActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ID_MAIN_LOADER = 111;
    private TextView account;
    private TextView amount;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.openSmsList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent smsListIntent = new Intent(MainActivity.this, SmsListActivity.class);
                startActivity(smsListIntent);
            }
        });
        findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportLoaderManager().initLoader(ID_MAIN_LOADER, null, MainActivity.this);
            }
        });

        account = findViewById(R.id.account);
        amount = findViewById(R.id.amount);
        setupSharedPreferences();
        getSupportLoaderManager().initLoader(ID_MAIN_LOADER, null, this);
   }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        account.setText(sharedPreferences.getString(getString(R.string.card_number_key), getString(R.string.card_number_default)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preference_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.card_number_key))) {
            account.setText(sharedPreferences.getString(getString(R.string.card_number_key), getString(R.string.card_number_default)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(ID_MAIN_LOADER, null, this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSupportLoaderManager().initLoader(ID_MAIN_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

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
                    return getContentResolver().query(SmsContract.SmsEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            SmsContract.SmsEntry._ID);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data." + e.getMessage());
                    System.out.println(e.getMessage());
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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToLast()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }
        String info = String.format("%s %s", data.getString(data.getColumnIndex(SmsContract.SmsEntry.COLUMN_REST)), CurrencyType.BYN.name());
        amount.setText(info);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //stub
    }
}