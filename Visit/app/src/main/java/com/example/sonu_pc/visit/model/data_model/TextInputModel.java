package com.example.sonu_pc.visit.model.data_model;

import android.support.v4.util.Pair;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sonupc on 14-01-2018.
 */

public class TextInputModel extends Model{

    private List<Pair<String,String>> text_input_data;

    public TextInputModel() {
    }

    public TextInputModel(List<Pair<String,String>> text_input_data) {
        this.text_input_data = text_input_data;
    }

    public List<Pair<String,String>> getText_input_data() {
        return text_input_data;
    }

    public void setText_input_data(List<Pair<String,String>> text_input_data) {
        this.text_input_data = text_input_data;
    }
}
