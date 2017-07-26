package com.johnmillercoding.cryptonotify.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.johnmillercoding.cryptonotify.R;
import com.johnmillercoding.cryptonotify.utilities.PreferenceManager;

public class SettingsActivity extends Activity {

    private EditText ethThreshold, zecThreshold, ltcThreshold;

    // Shared prefs
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initialize();
    }

    @SuppressWarnings("unchecked")
    private void initialize(){

        // Initializing Preferences
        preferenceManager = new PreferenceManager(this);

        // Initializing UI components
        Switch allNotifications = findViewById(R.id.allNotificationsSwitch);
        Switch ethNotifications = findViewById(R.id.ethNotificationsSwitch);
        Switch zecNotifications = findViewById(R.id.zecNotificationsSwitch);
        Switch ltcNotifications = findViewById(R.id.ltcNotificationsSwitch);
        ethThreshold = findViewById(R.id.ethThresholdEditText);
        zecThreshold = findViewById(R.id.zecThresholdEditText);
        ltcThreshold = findViewById(R.id.ltcThresholdEditText);
        Spinner ethThresholdCurrencySpinner = findViewById(R.id.ethThresholdCurrencySpinner);
        Spinner zecThresholdCurrencySpinner = findViewById(R.id.zecThresholdCurrencySpinner);
        Spinner ltcThresholdCurrencySpinner = findViewById(R.id.ltcThresholdCurrencySpinner);

        // Listeners
        allNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    preferenceManager.enableAllNotifications(true);
                }
                else {
                    preferenceManager.enableAllNotifications(false);
                }
            }
        });
        ethNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    preferenceManager.enableEthNotifications(true);
                }
                else {
                    preferenceManager.enableEthNotifications(false);
                }
            }
        });
        zecNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    preferenceManager.enableZecNotifications(true);
                }
                else {
                    preferenceManager.enableZecNotifications(false);
                }
            }
        });
        ltcNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    preferenceManager.enableLtcNotifications(true);
                }
                else {
                    preferenceManager.enableLtcNotifications(false);
                }
            }
        });
        ethThreshold.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    preferenceManager.setEthThreshold(ethThreshold.getText().toString());
                }
            }
        });
        zecThreshold.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    preferenceManager.setZecThreshold(zecThreshold.getText().toString());
                }
            }
        });
        ltcThreshold.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    preferenceManager.setLtcThreshold(ltcThreshold.getText().toString());
                }
            }
        });
        ethThresholdCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preferenceManager.setEthCurrency(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        zecThresholdCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preferenceManager.setZecCurrency(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        ltcThresholdCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preferenceManager.setLtcCurrency(adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Setting UI to preferred values
        allNotifications.setChecked(preferenceManager.allNotificationsEnabled());
        ethNotifications.setChecked(preferenceManager.ethNotificationsEnabled());
        zecNotifications.setChecked(preferenceManager.zecNotificationsEnabled());
        ltcNotifications.setChecked(preferenceManager.ltcNotificationsEnabled());
        ethThreshold.setText(preferenceManager.getEthThreshold());
        zecThreshold.setText(preferenceManager.getZecThreshold());
        ltcThreshold.setText(preferenceManager.getLtcThreshold());
        ethThresholdCurrencySpinner.setSelection(((ArrayAdapter<String>) ethThresholdCurrencySpinner.getAdapter()).getPosition(preferenceManager.getEthCurrency()));
        zecThresholdCurrencySpinner.setSelection(((ArrayAdapter<String>) zecThresholdCurrencySpinner.getAdapter()).getPosition(preferenceManager.getZecCurrency()));
        ltcThresholdCurrencySpinner.setSelection(((ArrayAdapter<String>) ltcThresholdCurrencySpinner.getAdapter()).getPosition(preferenceManager.getLtcCurrency()));

        // TODO Add Notification Interval Settings
    }
}
