package com.johnmillercoding.cryptonotify.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.johnmillercoding.cryptonotify.R;
import com.johnmillercoding.cryptonotify.services.NotificationService;
import com.johnmillercoding.cryptonotify.utilities.PreferenceManager;

/**
 * Settings Activity user interface.
 */
public class SettingsActivity extends Activity {

    // EditTexts
    private EditText ethThresholdEditText, zecThresholdEditText, ltcThresholdEditText;

    // Shared prefs
    private PreferenceManager preferenceManager;

    // Original preferences
    private boolean allNotifications, ethNotifications, ltcNotifications, zecNotifications;
    private String ethThreshold, ltcThreshold, zecThreshold, ethCurrency, ltcCurrency, zecCurrency, notificationUnit;
    private int notificationInterval;

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
        Switch allNotificationsSwitch = findViewById(R.id.allNotificationsSwitch);
        Switch ethNotificationsSwitch = findViewById(R.id.ethNotificationsSwitch);
        Switch zecNotificationsSwitch = findViewById(R.id.zecNotificationsSwitch);
        Switch ltcNotificationsSwitch = findViewById(R.id.ltcNotificationsSwitch);
        ethThresholdEditText = findViewById(R.id.ethThresholdEditText);
        zecThresholdEditText = findViewById(R.id.zecThresholdEditText);
        ltcThresholdEditText = findViewById(R.id.ltcThresholdEditText);
        Spinner ethThresholdCurrencySpinner = findViewById(R.id.ethThresholdCurrencySpinner);
        Spinner zecThresholdCurrencySpinner = findViewById(R.id.zecThresholdCurrencySpinner);
        Spinner ltcThresholdCurrencySpinner = findViewById(R.id.ltcThresholdCurrencySpinner);

        // Initializing original preferences
        allNotifications = preferenceManager.allNotificationsEnabled();
        ethNotifications = preferenceManager.ethNotificationsEnabled();
        ltcNotifications = preferenceManager.ltcNotificationsEnabled();
        zecNotifications = preferenceManager.zecNotificationsEnabled();
        ethThreshold = preferenceManager.getEthThreshold();
        ltcThreshold = preferenceManager.getLtcThreshold();
        zecThreshold = preferenceManager.getZecThreshold();
        ethCurrency = preferenceManager.getEthCurrency();
        ltcCurrency = preferenceManager.getLtcCurrency();
        zecCurrency = preferenceManager.getZecCurrency();
        notificationUnit = preferenceManager.getNotificationUnit();
        notificationInterval = preferenceManager.getNotificationInterval();

        // Listeners
        allNotificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        ethNotificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        zecNotificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        ltcNotificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
//        ethThresholdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    preferenceManager.setEthThreshold(ethThresholdEditText.getText().toString());
//                }
//            }
//        });
//        zecThresholdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    preferenceManager.setZecThreshold(zecThresholdEditText.getText().toString());
//                }
//            }
//        });
//        ltcThresholdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    preferenceManager.setLtcThreshold(ltcThresholdEditText.getText().toString());
//                }
//            }
//        });
        ethThresholdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                preferenceManager.setEthThreshold(ethThresholdEditText.getText().toString());
            }
        });
        zecThresholdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                preferenceManager.setZecThreshold(zecThresholdEditText.getText().toString());
            }
        });
        ltcThresholdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                preferenceManager.setLtcThreshold(ltcThresholdEditText.getText().toString());
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
        allNotificationsSwitch.setChecked(preferenceManager.allNotificationsEnabled());
        ethNotificationsSwitch.setChecked(preferenceManager.ethNotificationsEnabled());
        zecNotificationsSwitch.setChecked(preferenceManager.zecNotificationsEnabled());
        ltcNotificationsSwitch.setChecked(preferenceManager.ltcNotificationsEnabled());
        ethThresholdEditText.setText(preferenceManager.getEthThreshold());
        zecThresholdEditText.setText(preferenceManager.getZecThreshold());
        ltcThresholdEditText.setText(preferenceManager.getLtcThreshold());
        ethThresholdCurrencySpinner.setSelection(((ArrayAdapter<String>) ethThresholdCurrencySpinner.getAdapter()).getPosition(preferenceManager.getEthCurrency()));
        zecThresholdCurrencySpinner.setSelection(((ArrayAdapter<String>) zecThresholdCurrencySpinner.getAdapter()).getPosition(preferenceManager.getZecCurrency()));
        ltcThresholdCurrencySpinner.setSelection(((ArrayAdapter<String>) ltcThresholdCurrencySpinner.getAdapter()).getPosition(preferenceManager.getLtcCurrency()));
    }

    /**
     * Displays a dialog to set notification interval settings.
     * @param view the view.
     */
    public void showIntervalPicker(@SuppressWarnings("UnusedParameters") View view) {

        // Configuring dialog
        final Dialog dialog = new Dialog(SettingsActivity.this);
        dialog.setTitle("Notification Interval");
        dialog.setContentView(R.layout.dialog);

        // Configuring UI components
        Button okButton = dialog.findViewById(R.id.okButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        final NumberPicker intervalNumberPicker = dialog.findViewById(R.id.intervalNumberPicker);
        intervalNumberPicker.setMaxValue(100);
        intervalNumberPicker.setMinValue(1);
        intervalNumberPicker.setWrapSelectorWheel(true);
        final Spinner intervalUnitSpinner = dialog.findViewById(R.id.intervalUnitSpinner);
        ArrayAdapter<CharSequence> intervalUnitSpinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.interval_units));
        intervalUnitSpinner.setAdapter(intervalUnitSpinnerAdapter);

        // Setting preferred values
        intervalNumberPicker.setValue(preferenceManager.getNotificationInterval());
        int spinnerPosition = intervalUnitSpinnerAdapter.getPosition(preferenceManager.getNotificationUnit());
        intervalUnitSpinner.setSelection(spinnerPosition);


        // Listeners
        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                // Check if settings changed
                if (preferenceManager.getNotificationInterval() != intervalNumberPicker.getValue() || !preferenceManager.getNotificationUnit().equals(intervalUnitSpinner.getSelectedItem().toString())) {

                    // Set preferences and set changed flag
                    preferenceManager.setNotificationInterval(intervalNumberPicker.getValue());
                    preferenceManager.setNotificationUnit(intervalUnitSpinner.getSelectedItem().toString());
                }
                // Dismiss dialog
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                // Dismiss dialog
                dialog.dismiss();
            }
        });

        // Displaying dialog
        dialog.show();
    }

    private boolean settingsChanged(){

        // Check current preferences against original
        if (preferenceManager.allNotificationsEnabled() != allNotifications || preferenceManager.ethNotificationsEnabled() != ethNotifications || preferenceManager.ltcNotificationsEnabled() != ltcNotifications ||
                preferenceManager.zecNotificationsEnabled() != zecNotifications || !preferenceManager.getEthThreshold().equals(ethThreshold) || !preferenceManager.getLtcThreshold().equals(ltcThreshold) ||
                !preferenceManager.getZecThreshold().equals(zecThreshold) || !preferenceManager.getEthCurrency().equals(ethCurrency) || !preferenceManager.getLtcCurrency().equals(ltcCurrency) ||
                !preferenceManager.getZecCurrency().equals(zecCurrency) || preferenceManager.getNotificationInterval() != notificationInterval || !preferenceManager.getNotificationUnit().equals(notificationUnit)){

            // Settings changed
            return true;
        }
        // No changes
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Restart service if settings changed
        if (settingsChanged()){
            Toast.makeText(this, "Settings changed, restarting notification service...", Toast.LENGTH_SHORT).show();
            startService(new Intent(this, NotificationService.class));
        }
    }
}
