package com.example.sonu_pc.visit.model.preference_model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sonupc on 26-01-2018.
 */

public class MasterWorkflow {

    public static final String WORKFLOW_MAP_KEY = "workflows_map";
    private String instituteEmail;
    private Map<String, PreferencesModel> workflows_map;

    public MasterWorkflow() {
    }

    public MasterWorkflow(Map<String, PreferencesModel> workflows_map, String instituteEmail) {
        this.workflows_map = workflows_map;
        this.instituteEmail = instituteEmail;
    }

    public Map<String, PreferencesModel> getWorkflows_map() {
        return workflows_map;
    }

    public void setWorkflows_map(Map<String, PreferencesModel> workflows_map) {
        this.workflows_map = workflows_map;
    }

    public String getInstituteEmail() {
        return instituteEmail;
    }

    public void setInstituteEmail(String instituteEmail) {
        this.instituteEmail = instituteEmail;
    }
}
