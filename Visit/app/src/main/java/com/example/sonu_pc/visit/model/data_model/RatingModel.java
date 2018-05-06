package com.example.sonu_pc.visit.model.data_model;

import android.support.v4.util.Pair;

import java.util.List;
import java.util.Map;

public class RatingModel extends Model{

    //private String item1, item2, item3;
    private Map<String, String> rating_answers;
    public RatingModel() {
    }

    public RatingModel(Map<String, String> rating_answers) {
        this.rating_answers = rating_answers;
    }

    public Map<String, String> getRating_answers() {
        return rating_answers;
    }

    public void setRating_answers(Map<String, String> rating_answers) {
        this.rating_answers = rating_answers;
    }
}
