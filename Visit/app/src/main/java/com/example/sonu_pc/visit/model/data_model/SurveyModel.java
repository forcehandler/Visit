package com.example.sonu_pc.visit.model.data_model;

import android.support.v4.util.Pair;
import android.view.MotionEvent;

import java.util.List;
import java.util.Map;

/**
 * Created by sonupc on 12-01-2018.
 */

public class SurveyModel extends Model{

    //private String item1, item2, item3;
    private List<Pair<String,String>> survey_results;
    public SurveyModel() {
    }

    public SurveyModel(List<Pair<String,String>> survey_results) {
        this.survey_results = survey_results;
    }

    public List<Pair<String,String>> getSurvey_results() {
        return survey_results;
    }

    public void setSurvey_results(List<Pair<String,String>> survey_results) {
        this.survey_results = survey_results;
    }
}
