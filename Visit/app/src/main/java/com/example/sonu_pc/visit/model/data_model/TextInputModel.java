package com.example.sonu_pc.visit.model.data_model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sonupc on 14-01-2018.
 */

public class TextInputModel extends Model{

    private LinkedHashMap<String, String> text_input_data;

    public TextInputModel() {
    }

    public TextInputModel(LinkedHashMap<String, String> text_input_data) {
        this.text_input_data = text_input_data;
    }

    public Map<String, String> getText_input_data() {
        return text_input_data;
    }

    public void setText_input_data(LinkedHashMap<String, String> text_input_data) {
        this.text_input_data = text_input_data;
    }
}
