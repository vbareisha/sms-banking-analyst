package com.bareisha.smsbankinganalyst;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bareisha.smsbankinganalyst.model.contract.SmsContract;
import com.vbareisha.parser.core.enums.CurrencyType;

import static android.content.SharedPreferences.*;

public class MainActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener {

    private TextView account;
    private TextView amount;

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
        account = findViewById(R.id.account);
        amount = findViewById(R.id.amount);
        setupSharedPreferences();
        updateAmount();
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
        updateAmount();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateAmount();
    }

    private void updateAmount() {
        Cursor cursor = getContentResolver().query(SmsContract.SmsEntry.CONTENT_URI,
                null,
                null,
                null,
                SmsContract.SmsEntry._ID);
        if (cursor != null && cursor.isFirst()) {
            cursor.moveToLast();
            amount.setText(cursor.getString(cursor.getColumnIndex(SmsContract.SmsEntry.COLUMN_REST)) + " " + CurrencyType.BYN.name());
        }
    }
}