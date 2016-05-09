package com.leon.agriculturerobot.setting;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.leon.agriculturerobot.R;
import com.leon.agriculturerobot.utils.SerialPortFinder;

/**
 * Created by LML on 2016-05-07.
 */
public class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        SerialPortFinder serialPortFinder = new SerialPortFinder();
        String[] entries = serialPortFinder.getAllDevices();
        String[] entryValues = serialPortFinder.getAllDevicesPath();
        final ListPreference preferenceDevice = (ListPreference) findPreference("DEVICE");
        preferenceDevice.setSummary(preferenceDevice.getValue());
        preferenceDevice.setEntries(entries);
        preferenceDevice.setEntryValues(entryValues);
        preferenceDevice.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                return true;
            }
        });

        final ListPreference preferenceBaudrate = (ListPreference) findPreference("BAUDRATE");
        preferenceBaudrate.setSummary(preferenceBaudrate.getValue());
        preferenceBaudrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String)newValue);
                return true;
            }
        });

        final EditTextPreference preferenceIP = (EditTextPreference) findPreference("IP");
        preferenceIP.setSummary(preferenceIP.getText());
        preferenceIP.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String)newValue);
                return true;
            }
        });

        final EditTextPreference preferencePort = (EditTextPreference) findPreference("PORT");
        preferencePort.setSummary(preferencePort.getText());
        preferencePort.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String)newValue);
                return true;
            }
        });
    }
}
