package com.example.sonu_pc.visit;

import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
