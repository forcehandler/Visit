package com.example.sonu_pc.visit.model;

/**
 * Created by sonupc on 02-01-2018.
 */

public class PreferencesModel {

    public static final String SIGNUP_TIME_KEY = "signup_time";

    private boolean stage1, stage2, stage3, stage4, stage5, stage6;
    private String termsAndCond;
    private long signup_time;        // not really required

    private SurveyPreferenceModel surveyPreferenceModel;
    private TextInputPreferenceModel textInputPreferenceModel;

    public PreferencesModel() {
    }

    public PreferencesModel(boolean stage1, boolean stage2, boolean stage3, boolean stage4, boolean stage5, boolean stage6,
                            String termsAndCond, long signup_time, SurveyPreferenceModel surveyPreferenceModel,
                            TextInputPreferenceModel textInputPreferenceModel) {
        this.stage1 = stage1;
        this.stage2 = stage2;
        this.stage3 = stage3;
        this.stage4 = stage4;
        this.stage5 = stage5;
        this.stage6 = stage6;
        this.termsAndCond = termsAndCond;
        this.signup_time = signup_time;
        this.surveyPreferenceModel = surveyPreferenceModel;
        this.textInputPreferenceModel = textInputPreferenceModel;
    }

    public boolean isStage1() {
        return stage1;
    }

    public void setStage1(boolean stage1) {
        this.stage1 = stage1;
    }

    public boolean isStage2() {
        return stage2;
    }

    public void setStage2(boolean stage2) {
        this.stage2 = stage2;
    }

    public boolean isStage3() {
        return stage3;
    }

    public void setStage3(boolean stage3) {
        this.stage3 = stage3;
    }

    public boolean isStage4() {
        return stage4;
    }

    public void setStage4(boolean stage4) {
        this.stage4 = stage4;
    }

    public boolean isStage5() {
        return stage5;
    }

    public void setStage5(boolean stage5) {
        this.stage5 = stage5;
    }

    public boolean isStage6() {
        return stage6;
    }

    public void setStage6(boolean stage6) {
        this.stage6 = stage6;
    }

    public String getTermsAndCond() {
        return termsAndCond;
    }

    public void setTermsAndCond(String termsAndCond) {
        this.termsAndCond = termsAndCond;
    }

    public long getSignup_time() {
        return signup_time;
    }

    public void setSignup_time(long signup_time) {
        this.signup_time = signup_time;
    }

    public SurveyPreferenceModel getSurveyPreferenceModel() {
        return surveyPreferenceModel;
    }

    public void setSurveyPreferenceModel(SurveyPreferenceModel surveyPreferenceModel) {
        this.surveyPreferenceModel = surveyPreferenceModel;
    }

    public TextInputPreferenceModel getTextInputPreferenceModel() {
        return textInputPreferenceModel;
    }

    public void setTextInputPreferenceModel(TextInputPreferenceModel textInputPreferenceModel) {
        this.textInputPreferenceModel = textInputPreferenceModel;
    }
}
