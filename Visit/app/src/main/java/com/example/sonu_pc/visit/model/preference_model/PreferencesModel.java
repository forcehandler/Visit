package com.example.sonu_pc.visit.model.preference_model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sonupc on 02-01-2018.
 */

public class PreferencesModel {

    //public static final String SIGNUP_TIME_KEY = "signup_time";

    private long signup_time;        // not really required

    private boolean isWorkflowForSignOut;
    // Map of order of screens and the Preference of the screen
    private ArrayList<Preference> order_of_screens;

    public PreferencesModel() {}

    public PreferencesModel(long signup_time, ArrayList<Preference> order_of_screens, boolean isWorkflowForSignOut) {

        this.signup_time = signup_time;
        this.order_of_screens = order_of_screens;
        this.isWorkflowForSignOut = isWorkflowForSignOut;
    }

    public long getSignup_time() {
        return signup_time;
    }

    public void setSignup_time(long signup_time) {
        this.signup_time = signup_time;
    }

    public ArrayList<Preference> getOrder_of_screens() {
        return order_of_screens;
    }

    public void setOrder_of_screens(ArrayList<Preference> order_of_screens) {
        this.order_of_screens = order_of_screens;
    }

    public boolean isWorkflowForSignOut() {
        return isWorkflowForSignOut;
    }

    public void setWorkflowForSignOut(boolean workflowForSignOut) {
        isWorkflowForSignOut = workflowForSignOut;
    }
}
