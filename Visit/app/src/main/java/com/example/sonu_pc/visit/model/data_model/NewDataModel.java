package com.example.sonu_pc.visit.model.data_model;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by sonupc on 29-01-2018.
 */

public class NewDataModel {

    private Map<String, Map<String, Model>> visitor_model;

    public NewDataModel() {
    }

    public Map<String, Map<String, Model>> getVisitor_model() {
        return visitor_model;
    }

    public void setVisitor_model(Map<String, Map<String, Model>> visitor_model) {
        this.visitor_model = visitor_model;
    }
}
