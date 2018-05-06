package com.example.sonu_pc.visit.model.preference_model;

public class SuggestionPreference extends Preference{
    private String suggestion_text;
    public final String wipe = "CLASS_SUGGESTION";

    public SuggestionPreference() {
    }

    public String getSuggestion_text() {
        return suggestion_text;
    }

    public void setSuggestion_text(String suggestion_text) {
        this.suggestion_text = suggestion_text;
    }

    public String getWipe() {
        return wipe;
    }
}
