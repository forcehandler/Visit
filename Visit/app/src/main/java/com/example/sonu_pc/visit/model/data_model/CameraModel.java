package com.example.sonu_pc.visit.model.data_model;

import android.net.Uri;
import android.support.v4.util.Pair;

import java.util.Map;

/**
 * Created by sonupc on 14-01-2018.
 */

public class CameraModel {

    private Pair<String, Uri> cameraKeyUriMap;

    public CameraModel() {}

    public Pair<String, Uri> getCameraKeyUriPair() {
        return cameraKeyUriMap;
    }

    public void setCameraKeyUriPair(Pair<String, Uri> cameraKeyUriMap) {
        this.cameraKeyUriMap = cameraKeyUriMap;
    }


}
