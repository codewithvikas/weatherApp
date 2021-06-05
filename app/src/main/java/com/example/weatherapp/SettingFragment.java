package com.example.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;


public class SettingFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            Preference preference = findPreference(key);
            if (preference!=null){
                if (!(preference instanceof CheckBoxPreference)){
                    setPreferenceSummary(preference,sharedPreferences.getString(preference.getKey(),""));
                }
            }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_general);

            SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
            PreferenceScreen preferenceScreen = getPreferenceScreen();

            int count = preferenceScreen.getPreferenceCount();
            for (int i = 0 ;i<count;i++){
                Preference p = preferenceScreen.getPreference(i);
                if (!(p instanceof CheckBoxPreference)){
                    String value = sharedPreferences.getString(p.getKey(),"");
                    setPreferenceSummary(p,value);
                }
            }
    }

    private void setPreferenceSummary(Preference p, Object value) {

        String stringValue = String.valueOf(value);
        String key = p.getKey();

        if (p instanceof ListPreference){
            ListPreference listPreference = (ListPreference)p;

            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex>=0){
                p.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
        else {
            p.setSummary(stringValue);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
