package com.example.sonu_pc.visit.model.data_model;

import android.view.MotionEvent;

import java.util.Map;

/**
 * Created by sonupc on 12-01-2018.
 */

public class SurveyModel extends Model{

    //private String item1, item2, item3;
    private Map<String, String> survey_results;
    public SurveyModel() {
    }

    public SurveyModel(Map<String, String> survey_results) {
        this.survey_results = survey_results;
    }

    public Map<String, String> getSurvey_results() {
        return survey_results;
    }

    public void setSurvey_results(Map<String, String> survey_results) {
        this.survey_results = survey_results;
    }
}
