package com.example.sonu_pc.visit.model.data_model;

import java.util.Map;

public class SuggestionModel extends Model {

    private Map<String, String> suggestions_map;

    public SuggestionModel() {
    }

    public SuggestionModel(Map<String, String> suggestions_map) {
        this.suggestions_map = suggestions_map;
    }

    public Map<String, String> getSuggestions_map() {
        return suggestions_map;
    }

    public void setSuggestions_map(Map<String, String> suggestions_map) {
        this.suggestions_map = suggestions_map;
    }
}
