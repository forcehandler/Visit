package com.example.sonu_pc.visit.model.preference_model;


import java.util.List;

/*Stores the list of questions to get the user ratings for*/
public class RatingPreferenceModel extends Preference {

    private List<String> questions;
    public final String wipe = "CLASS_RATING";


    public RatingPreferenceModel() {}

    public RatingPreferenceModel(List<String> questions) {
        this.questions = questions;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public String getWipe() {
        return wipe;
    }
}
