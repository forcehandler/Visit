package com.example.sonu_pc.visit.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.model.PreferencesModel;
import com.google.gson.Gson;

/**
 * Created by sonupc on 14-01-2018.
 */

public class VisitUtils {

    public static PreferencesModel getPreferences(Context context){
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(context.getString(R.string.REMOTE_CONFIG_PREFERENCE), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String preference_object_json = sharedPreferences.getString(context.getString(R.string.CONFIGURATION_PREFERENCE_KEY), "");

        PreferencesModel preferencesModel = gson.fromJson(preference_object_json, PreferencesModel.class);

        return preferencesModel;
    }
}
