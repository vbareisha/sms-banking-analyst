package com.bareisha.smsbankinganalyst;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bareisha.smsbankinganalyst.loader.SmsScanerFromDevice;
import com.bareisha.smsbankinganalyst.model.contract.SmsContract;
import com.bareisha.smsbankinganalyst.service.SmsLoadingService;
import com.vbareisha.parser.core.enums.CurrencyType;

import static android.content.SharedPreferences.*;

public class MainActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_MAIN_LOADER = 111;
    private static final int SMS_PERMISSION_CODE = 10;
    private static final int SMS_PERMISSION_READ = 20;
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
                getSupportLoaderManager().restartLoader(ID_MAIN_LOADER, null, MainActivity.this);
            }
        });
        findViewById(R.id.btnSync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check permissions for reading sms
                //todo опять права  - нарисовать нормальное окно с правами!!!!
                if (!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)) {
                    showRequestPermissionsInfoDialogForReadingSms();
                }
                SmsScanerFromDevice scaner = new SmsScanerFromDevice(view.getContext());
                getSupportLoaderManager().initLoader(SmsScanerFromDevice.ID_SCANER_SMS_FROM_DEVICE_LOADER, null, scaner);
            }
        });

        account = findViewById(R.id.account);
        amount = findViewById(R.id.amount);

        setupSharedPreferences();
        getSupportLoaderManager().initLoader(ID_MAIN_LOADER, null, this);
        //todo другое место
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)) {
            showRequestPermissionsInfoAlertDialog();
        }
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return loaderFactory(this);
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

    private void showRequestPermissionsInfoAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("title");
        builder.setMessage("message");
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (!(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECEIVE_SMS)) {
                        Log.i(MainActivity.TAG, "get perm!");
                    }
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.RECEIVE_SMS}, SMS_PERMISSION_CODE);
                }
            }
        });
        builder.show();
    }

    // dialog for getting read sms permissions
    private void showRequestPermissionsInfoDialogForReadingSms() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permison_read_sms_title);
        builder.setMessage(R.string.premission_read_sms_text);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_SMS)) {
                    Log.d(MainActivity.TAG, "Getting permission for reading sms!");
                }
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_SMS}, SMS_PERMISSION_READ);
            }
        });
        builder.show();
    }

    private static Loader<Cursor> loaderFactory(final Context context) {
        return new AsyncTaskLoader<Cursor>(context) {

            @Override
            protected void onStartLoading() {
                // Force a new load
                forceLoad();
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
                    System.out.println(e.getMessage());
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                super.deliverResult(data);
            }
        };
    }
}