package com.example.sonu_pc.visit.activities;

import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.sonu_pc.visit.R;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);

        Log.d(TAG, "onCreate()");
        addPreferencesFromResource(R.xml.preferences);
        /*getSupportFragmentManager().beginTransaction().replace(R.id.settings_fragment_container, new SettingsFragment())
                .commit();*/
    }



   /* public static class SettingsFragment extends PreferenceFragment {

        private static final String TAG = SettingsFragment.class.getSimpleName();

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }*/
}
