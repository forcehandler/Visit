package com.example.sonu_pc.visit.model;

import java.util.Map;

/**
 * Created by sonupc on 12-01-2018.
 */

public class SurveyModel {

    //private String item1, item2, item3;
    private Map<String, String> survey_results;
    public SurveyModel() {
    }

    public SurveyModel(Map<String, String> survey_results) {
        //.item1 = item1;
        //this.item2 = item2;
        //this.item3 = item3;
        this.survey_results = survey_results;
    }

   /* public String getItem1() {
        return item1;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public String getItem2() {
        return item2;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }

    public String getItem3() {
        return item3;
    }

    public void setItem3(String item3) {
        this.item3 = item3;
    }*/

    public Map<String, String> getSurvey_results() {
        return survey_results;
    }

    public void setSurvey_results(Map<String, String> survey_results) {
        this.survey_results = survey_results;
    }
}
