package com.example.sonu_pc.visit.model.data_model;

/**
 * Created by sonupc on 14-01-2018.
 */

public class DataModel {

    private SurveyModel surveyModel;
    private TextInputModel textInputModel;
    private CameraModel cameraModel;

    public DataModel() {
    }

    public SurveyModel getSurveyModel() {
        return surveyModel;
    }

    public void setSurveyModel(SurveyModel surveyModel) {
        this.surveyModel = surveyModel;
    }

    public TextInputModel getTextInputModel() {
        return textInputModel;
    }

    public void setTextInputModel(TextInputModel textInputModel) {
        this.textInputModel = textInputModel;
    }

    public CameraModel getCameraModel() {
        return cameraModel;
    }

    public void setCameraModel(CameraModel cameraModel) {
        this.cameraModel = cameraModel;
    }
}
