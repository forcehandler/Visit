package com.example.sonu_pc.visit.activities;

import android.content.Context;
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
import com.example.sonu_pc.visit.fragments.WelcomeFragment;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StageActivity extends AppCompatActivity implements WelcomeFragment.OnWelcomeFragmentInteractionListener,
        VisitorInfoFragment.OnFragmentInteractionListener,
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
        private Map<String, Model> mDataModelsMap;

        private int curr_stage = 0;

        //######################################################################################
        private LinkedHashMap<String, String> quesAnsMap;
        private CollectionReference workflowCollectionRef;
        private CollectionReference visitorsCollectionRef;

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
            mDataModelsMap = new HashMap<>();

            //############################################################################
            quesAnsMap = new LinkedHashMap<>();


            //############################################################################

            initFirestore();

            // Show the fragment with list of screens.
            initWelcomeFragment();

            // Obtain MasterWorkflow
            initMasterWorkflow();

        }

        private void initFirestore(){
            Log.d(TAG, "initFirestore()");
            mFirestore = FirebaseFirestore.getInstance();

            // Enable offline
        }

        private void initWelcomeFragment(){
            Log.d(TAG, "initWelcomeFragment()");
            // Set the curr_stage to 0
            curr_stage = 0;
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, WelcomeFragment.newInstance("",""))
                    .commit();
        }

        private void initMasterWorkflow(){

            Log.d(TAG, "initMasterWorkflow()");
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

        private void initSessionWorkflow(String selected_workflow_key){

            workflow_name = selected_workflow_key;
            Log.d(TAG, "initSessionWorkflow()");

            mSelectedWorkflow = masterWorkflow.getWorkflows_map().get(workflow_name);
            if(mSelectedWorkflow instanceof PreferencesModel){
                mOrderOfScreens = mSelectedWorkflow.getOrder_of_screens();
                Log.d(TAG, "obtained the selected workflow model");
            }
            else{
                Log.e(TAG, "Did not obtain the selected workflow model");
            }

            // TODO: Integrating Welcome fragment in stage activity
            //Start handling fragments after initializing session workflow given from welcome
            // fragment
            handleFragments();

        }

        private void handleFragments(){

            Log.d(TAG, "handleFragments()");

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

                    // TODO :remove this data model
                    //uploadDataModel();


                    //#################################################################
                    uploadWorkflow(workflow_name);
                }

            }
            else{ // Exhausted the screens, upload the data
                Log.d(TAG, "exhausted all the screens");

            }

        }


        @Override
        public void onWelcomeFragmentInteraction(String selected_workflow_key) {
            // set the selected workflow
            initSessionWorkflow(selected_workflow_key);
        }

        @Override
        public void onFragmentInteraction(int direction, int stageNo) {
            Log.d(TAG, "onFragmentInteraction()");
            handleFragments();
        }


        @Override
        public void onTextInputInteraction(TextInputModel textInputModel) {
            Log.d(TAG, "obtained textinput model");
            if(mCurrentPreference instanceof TextInputPreferenceModel){
                mDataModelsMap.put(((TextInputPreferenceModel) mCurrentPreference).getPage_title(), textInputModel);

                //#######################################################################
                quesAnsMap.putAll(textInputModel.getText_input_data());
            }
            else{
                Log.e(TAG, "current preference object did not match the required object, check onTextInputInteraction");
            }

        }

        @Override
        public void onSurveyInteraction(SurveyModel surveyModel) {
            Log.d(TAG, "obtained survey info");
            if(mCurrentPreference instanceof SurveyPreferenceModel){
                mDataModelsMap.put(((SurveyPreferenceModel) mCurrentPreference).getSurvey_title(), surveyModel);

                //########################################################################
                quesAnsMap.putAll(surveyModel.getSurvey_results());
            }
            else{
                Log.e(TAG, "current preference object did not match the required object, check onSurveyInteraction");
            }
        }

        @Override
        public void OnThankYouFragmentInteraction() {

            // TODO: Integrating Welcome fragment in stage activity
            /*Intent intent = new Intent(this, MasterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
            initWelcomeFragment();
        }

        private void uploadDataModel(){

            Map<String, Map<String, Model>> visitor_data_map = new HashMap<>();
            visitor_data_map.put(workflow_name, mDataModelsMap);
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


        //############################################################################3
        private void uploadWorkflow(final String workflow){
            workflowCollectionRef = mFirestore.collection(getString(R.string.collection_ref_institutes))
                    .document(FirebaseAuth.getInstance().getUid()).collection(getString(R.string.collection_ref_workflows));
            visitorsCollectionRef = workflowCollectionRef.document(workflow).collection(getString(R.string.collection_ref_visitors));

            // Get the time of signUp
            String visitor_id = System.currentTimeMillis() + "";  //for now this will be the visitor uid

            ArrayList<String> questions = new ArrayList<>(quesAnsMap.keySet());
            Map<String, List<String>> questionsMap = new HashMap<>();
            questionsMap.put("questions", questions);
            workflowCollectionRef.document(workflow).set(questionsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "uplaoded the list of questions in " + workflow + " workflow");
                }
            });

            visitorsCollectionRef.document(visitor_id).set(quesAnsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "Uploaded the visitor ques Ans map");
                }
            });
        }

        //##########################################################################################

        private void showSnackbar(String message){
            Snackbar.make(mConstraintLayout, message, Snackbar.LENGTH_SHORT).show();
        }


}
