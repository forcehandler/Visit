package com.example.sonu_pc.visit.utils;

import com.example.sonu_pc.visit.model.preference_model.CameraPreference;
import com.example.sonu_pc.visit.model.preference_model.Preference;
import com.example.sonu_pc.visit.model.preference_model.SurveyPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.TextInputPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.ThankYouPreference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by sonupc on 29-01-2018.
 */

public class GsonUtils {

    public static Gson getGsonParser(){

        RuntimeTypeAdapterFactory<Preference> adapter =
                RuntimeTypeAdapterFactory
                        .of(Preference.class)
                        .registerSubtype(TextInputPreferenceModel.class)
                        .registerSubtype(SurveyPreferenceModel.class)
                        .registerSubtype(ThankYouPreference.class)
                        .registerSubtype(CameraPreference.class);


        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(adapter).create();

        return gson;
    }
}
