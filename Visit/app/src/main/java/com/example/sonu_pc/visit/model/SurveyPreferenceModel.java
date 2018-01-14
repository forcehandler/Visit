package com.example.sonu_pc.visit.model;

import java.util.ArrayList;

/**
 * Created by sonupc on 13-01-2018.
 */

public class SurveyPreferenceModel {

    private String survey_title;
    private ArrayList<String> survey_item_name;
    private ArrayList<ArrayList<String>> survey_item_options;

    public SurveyPreferenceModel() {
    }

    public SurveyPreferenceModel(String survey_title, ArrayList<String> survey_item_name, ArrayList<ArrayList<String>> survey_item_options) {
        this.survey_title = survey_title;
        this.survey_item_name = survey_item_name;
        this.survey_item_options = survey_item_options;
    }

    public String getSurvey_title() {
        return survey_title;
    }

    public void setSurvey_title(String survey_title) {
        this.survey_title = survey_title;
    }

    public ArrayList<String> getSurvey_item_name() {
        return survey_item_name;
    }

    public void setSurvey_item_name(ArrayList<String> survey_item_name) {
        this.survey_item_name = survey_item_name;
    }

    public ArrayList<ArrayList<String>> getSurvey_item_options() {
        return survey_item_options;
    }

    public void setSurvey_item_options(ArrayList<ArrayList<String>> survey_item_options) {
        this.survey_item_options = survey_item_options;
    }
}
