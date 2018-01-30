package com.example.sonu_pc.visit.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.sonu_pc.visit.model.data_model.SurveyModel;
import com.google.firebase.firestore.Exclude;

/**
 * Created by sonu-pc on 20-12-2017.
 */


public class CouponModel implements Parcelable {

    private String company_name;
    private String visitor_name;
    private String visitee_name;
    private String visitee_position;
    private String visitor_uid;
    private SurveyModel surveyModel;
    private Bitmap visitor_face_photo;
    private Bitmap visitor_id_photo;

    public CouponModel() {
    }

    public CouponModel(SurveyModel surveyModel, String company_name, String visitor_name, String visitee_name, String visitee_position, String visitor_uid, Bitmap visitor_face_photo, Bitmap visitor_id_photo) {
        this.surveyModel = surveyModel;
        this.company_name = company_name;
        this.visitor_name = visitor_name;
        this.visitee_name = visitee_name;
        this.visitee_position = visitee_position;
        this.visitor_uid = visitor_uid;
        this.visitor_face_photo = visitor_face_photo;
        this.visitor_id_photo = visitor_id_photo;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getVisitor_name() {
        return visitor_name;
    }

    public void setVisitor_name(String visitor_name) {
        this.visitor_name = visitor_name;
    }

    public String getVisitee_name() {
        return visitee_name;
    }

    public void setVisitee_name(String visitee_name) {
        this.visitee_name = visitee_name;
    }

    public String getVisitee_position() {
        return visitee_position;
    }

    public void setVisitee_position(String visitee_position) {
        this.visitee_position = visitee_position;
    }

    public String getVisitor_uid() {
        return visitor_uid;
    }

    public void setVisitor_uid(String visitor_uid) {
        this.visitor_uid = visitor_uid;
    }

    public SurveyModel getSurveyModel() {
        return surveyModel;
    }

    public void setSurveyModel(SurveyModel surveyModel) {
        this.surveyModel = surveyModel;
    }

    @Exclude
    public Bitmap getVisitor_id_photo() {
        return visitor_id_photo;
    }

    public void setVisitor_id_photo(Bitmap visitor_id_photo) {
        this.visitor_id_photo = visitor_id_photo;
    }

    @Exclude
    public Bitmap getVisitor_face_photo() {
        return visitor_face_photo;
    }

    public void setVisitor_face_photo(Bitmap visitor_face_photo) {
        this.visitor_face_photo = visitor_face_photo;
    }

    protected CouponModel(Parcel in) {
        company_name = in.readString();
        visitor_name = in.readString();
        visitee_name = in.readString();
        visitee_position = in.readString();
        visitor_uid = in.readString();
        visitor_face_photo = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        visitor_id_photo = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(company_name);
        dest.writeString(visitor_name);
        dest.writeString(visitee_name);
        dest.writeString(visitee_position);
        dest.writeString(visitor_uid);
        dest.writeValue(visitor_face_photo);
        dest.writeValue(visitor_id_photo);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CouponModel> CREATOR = new Parcelable.Creator<CouponModel>() {
        @Override
        public CouponModel createFromParcel(Parcel in) {
            return new CouponModel(in);
        }

        @Override
        public CouponModel[] newArray(int size) {
            return new CouponModel[size];
        }
    };
}