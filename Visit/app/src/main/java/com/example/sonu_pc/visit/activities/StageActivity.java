package com.example.sonu_pc.visit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.fragments.FaceIdFragment;
import com.example.sonu_pc.visit.fragments.IdScanFragment;
import com.example.sonu_pc.visit.fragments.NonDisclosureFragment;
import com.example.sonu_pc.visit.fragments.SurveyFragment;
import com.example.sonu_pc.visit.fragments.ThankYouFragment;
import com.example.sonu_pc.visit.fragments.VisiteeInfoFragment;
import com.example.sonu_pc.visit.fragments.VisitorInfoFragment;
import com.example.sonu_pc.visit.model.data_model.Model;
import com.example.sonu_pc.visit.model.data_model.NewDataModel;
import com.example.sonu_pc.visit.model.data_model.SurveyModel;
import com.example.sonu_pc.visit.model.data_model.TextInputModel;
import com.example.sonu_pc.visit.model.preference_model.MasterWorkflow;
import com.example.sonu_pc.visit.model.preference_model.Preference;
import com.example.sonu_pc.visit.model.preference_model.PreferencesModel;
import com.example.sonu_pc.visit.model.preference_model.SurveyPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.TextInputPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.ThankYouPreference;
import com.example.sonu_pc.visit.utils.GsonUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StageActivity extends AppCompatActivity implements VisitorInfoFragment.OnFragmentInteractionListener,
        FaceIdFragment.OnFragmentInteractionListener, NonDisclosureFragment.OnFragmentInteractionListener,
        VisitorInfoFragment.OnVisitorInteractionListener,
        VisiteeInfoFragment.OnFragmentInteractionListener, IdScanFragment.OnFragmentInteractionListener ,
        SurveyFragment.OnSurveyInteractionListener, SurveyFragment.OnFragmentInteractionListener,
        ThankYouFragment.OnThankYouFragmentInteractionListener {

        private static final String TAG = StageActivity.class.getSimpleName();

        private NewDataModel mVisitorDataModel;

        private FirebaseFirestore mFirestore;

        private MasterWorkflow masterWorkflow;

        private ConstraintLayout mConstraintLayout;

        private String workflow_name;

        private PreferencesModel mSelectedWorkflow;
        private Preference mCurrentPreference;

        private ArrayList<Preference> mOrderOfScreens;
        private Map<String, Model> mDataModels;

        private static int curr_stage = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_stage);

            // Initialize the views
            mConstraintLayout = findViewById(R.id.constraint_container_signup);

            // replacement for coupon model
            mVisitorDataModel = new NewDataModel();
            mDataModels = new HashMap<>();


            initFirestore();

            initMasterWorkflow();

            initProjectWorkflow();

            handleFragments();
        }


        private void initFirestore(){
            mFirestore = FirebaseFirestore.getInstance();

            // Enable offline
        }

        private void initMasterWorkflow(){
            SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE_MASTERWORKFLOW), Context.MODE_PRIVATE);
            Gson gson2 = GsonUtils.getGsonParser();
            String workflow_json = preferences.getString(getString(R.string.PREF_KEY_MASTERWORKFLOW), "NOPREF");
            Log.d(TAG, "obtained_workflow_json = " + workflow_json);
            if(workflow_json == "NOPREF"){
                Log.e(TAG, "Could not fetch the workflow json from sharedpreferences");
            }
            else{
                masterWorkflow = gson2.fromJson(workflow_json, MasterWorkflow.class);
            }
        }

        private void initProjectWorkflow(){
            // get the workflow name
            Intent intent = getIntent();
            workflow_name = intent.getStringExtra(getString(R.string.INTENT_WORKFLOW_SELECT_KEY));

            mSelectedWorkflow = masterWorkflow.getWorkflows_map().get(workflow_name);
            if(mSelectedWorkflow instanceof PreferencesModel){
                mOrderOfScreens = mSelectedWorkflow.getOrder_of_screens();
                Log.d(TAG, "obtained the selected workflow model");
            }
            else{
                Log.e(TAG, "Did not obtain the selected workflow model");
            }

        }

        private void handleFragments(){

            /*Gson gson2 = GsonUtils.getGsonParser();
            for(Preference preference : mOrderOfScreens){
                Log.d(TAG, "galalal");

                if(preference instanceof TextInputPreferenceModel){
                    Log.d(TAG, "text input pref " + ((TextInputPreferenceModel) preference).getPage_title());
                }
                if(preference instanceof SurveyPreferenceModel){
                    Log.d(TAG, "survey pref " + ((SurveyPreferenceModel) preference).getSurvey_title());
                }
            }*/

            if(curr_stage < mOrderOfScreens.size()) { // if the curr screen no is within req no of screens
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

                Log.d(TAG, "curr stage = " + curr_stage);
                Gson gson = GsonUtils.getGsonParser();
                String pref_obj_json;

                mCurrentPreference = mOrderOfScreens.get(curr_stage++);      // get the pref object of the fragment

                if(mCurrentPreference instanceof TextInputPreferenceModel){
                    pref_obj_json = gson.toJson((TextInputPreferenceModel) mCurrentPreference);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, VisitorInfoFragment.newInstance(pref_obj_json, ""))
                            .commit();
                }
                else if(mCurrentPreference instanceof SurveyPreferenceModel){
                    pref_obj_json = gson.toJson((SurveyPreferenceModel) mCurrentPreference);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, SurveyFragment.newInstance(pref_obj_json, ""))
                            .commit();
                }
                else if(mCurrentPreference instanceof ThankYouPreference){
                    pref_obj_json = gson.toJson((ThankYouPreference) mCurrentPreference);
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, ThankYouFragment.newInstance(pref_obj_json))
                            .commit();
                    Log.d(TAG, "uploading new visitor data model");
                    uploadDataModel();
                }

            }
            else{ // Exhausted the screens, upload the data
                Log.d(TAG, "exhausted all the screens");

            }

        }


        @Override
        public void onFragmentInteraction(int direction, int stageNo) {
            Log.d(TAG, "onFragmentInteraction()");
            handleFragments();
        }

        @Override
        public void onVisitorInteraction(String name, String company, String phoneNo) {

        }

        @Override
        public void onTextInputInteraction(TextInputModel textInputModel) {
            Log.d(TAG, "obtained textinput model");
            if(mCurrentPreference instanceof TextInputPreferenceModel){
                mDataModels.put(((TextInputPreferenceModel) mCurrentPreference).getPage_title(), textInputModel);
            }
            else{
                Log.e(TAG, "current preference object did not match the required object, check onTextInputInteraction");
            }

        }

        @Override
        public void onSurveyInteraction(SurveyModel surveyModel) {
            Log.d(TAG, "obtained survey info");
            if(mCurrentPreference instanceof SurveyPreferenceModel){
                mDataModels.put(((SurveyPreferenceModel) mCurrentPreference).getSurvey_title(), surveyModel);
            }
            else{
                Log.e(TAG, "current preference object did not match the required object, check onSurveyInteraction");
            }
        }

        @Override
        public void OnThankYouFragmentInteraction() {
            Intent intent = new Intent(this, MasterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        private void uploadDataModel(){

            Map<String, Map<String, Model>> visitor_data_map = new HashMap<>();
            visitor_data_map.put(workflow_name, mDataModels);
            mVisitorDataModel.setVisitor_model(visitor_data_map);

            CollectionReference visitor_reference = mFirestore.collection("institutes")
                    .document(FirebaseAuth.getInstance().getUid()).collection("visitors");

            // Get the time of signUp
            String visitor_id = System.currentTimeMillis() + "";  //for now this will be the visitor uid

            visitor_reference.document(visitor_id).set(mVisitorDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //showSnackbar("Successfully uploaded the data model");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //showSnackbar("Failed to addd the data model");
                }
            });
        }

        private void showSnackbar(String message){
            Snackbar.make(mConstraintLayout, message, Snackbar.LENGTH_SHORT).show();
        }


}
