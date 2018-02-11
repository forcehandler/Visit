package com.example.sonu_pc.visit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.model.data_model.CameraModel;
import com.example.sonu_pc.visit.model.preference_model.CameraPreference;
import com.example.sonu_pc.visit.model.preference_model.MasterWorkflow;
import com.example.sonu_pc.visit.model.preference_model.Preference;
import com.example.sonu_pc.visit.model.preference_model.PreferencesModel;
import com.example.sonu_pc.visit.model.preference_model.SurveyPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.TextInputPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.ThankYouPreference;
import com.example.sonu_pc.visit.services.TextToSpeechService;
import com.example.sonu_pc.visit.utils.GsonUtils;
import com.example.sonu_pc.visit.utils.VisitUtils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SetupActivity extends AppCompatActivity {

    private static final String TAG = SetupActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 124;

    private DatabaseReference mDatabaseReference;

    private ConstraintLayout mConstraintLayout;
    private ProgressBar mProgressBar;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_setup);

        mConstraintLayout = findViewById(R.id.constraint_container_setup);
        mProgressBar = findViewById(R.id.progress_bar);
        mImageView = findViewById(R.id.brand_logo);

        startSigninSequence();
    }

    private void startSigninSequence(){

        Log.d(TAG, "starting sign in sequence");
        FirebaseApp.initializeApp(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            Snackbar.make(mConstraintLayout, "Already Signed in", Snackbar.LENGTH_SHORT)
                    .show();

            // Obtain the config values
            //TODO: for testing masterworkflow object
            //getConfigValues();

            // TODO: for testing only, added always on registration and commented get config vavlues
            //test_registerInstitutionForRemoteConfig();
            test_getConfigValues();

            //test_firebase_so_ques();

        }
        else {
            // not signed in
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder().build(),
                    RC_SIGN_IN);
        }

        startTtsService();
    }

    private void startTtsService(){
        Log.d(TAG, "start TtsService");
        Intent serviceIntent = new Intent(this, TextToSpeechService.class);
        this.startService(serviceIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if(resultCode == RESULT_OK){
                Snackbar.make(mConstraintLayout, "Successfully Signed In", Snackbar.LENGTH_SHORT)
                        .show();
                // First time registration of institution on realtime database for remote config
                //TODO: for testing masterworkflow object
                //registerInstitutionForRemoteConfig();

                test_registerInstitutionForRemoteConfig();

            }
            else{
                // Sign in failed
                if(response == null){
                    // user pressed back button
                }
            }
        }
    }




    private void registerInstitutionForRemoteConfig(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Register with default config values
        PreferencesModel preferencesModel = new PreferencesModel();
        preferencesModel.setSignup_time(System.currentTimeMillis());
        preferencesModel.setStage1(true);
        preferencesModel.setStage2(true);
        preferencesModel.setStage3(false);
        preferencesModel.setStage4(false);
        preferencesModel.setStage5(false);
        //preferencesModel.setStage6(true);
        preferencesModel.setTermsAndCond("These are the terms and dsafkljsa;fa conditions :D");

        // Create default survey values
        SurveyPreferenceModel surveyPreferenceModel = new SurveyPreferenceModel();
        String survey_title = "Sample survey";
        ArrayList<String> survey_item_titles = new ArrayList<>();
        survey_item_titles.add("Sample item 1");
        survey_item_titles.add("Sample item 2");
        survey_item_titles.add("Sample item 3");

        ArrayList<String> options1 = new ArrayList<String>();
        ArrayList<String> options2 = new ArrayList<String>();
        ArrayList<String> options3 = new ArrayList<String>();

        options1.add("options11");
        options1.add("options12");
        options1.add("options13");

        options2.add("options21");
        options2.add("options22");
        options2.add("options23");

        options3.add("options31");
        options3.add("options32");
        options3.add("options33");

        ArrayList<ArrayList<String>> survey_item_options = new ArrayList<>();
        survey_item_options.add(options1);
        survey_item_options.add(options2);
        survey_item_options.add(options3);

        surveyPreferenceModel.setSurvey_title(survey_title);
        surveyPreferenceModel.setSurvey_item_name(survey_item_titles);
        surveyPreferenceModel.setSurvey_item_options(survey_item_options);

        // Create default TextInputPreference Model
        TextInputPreferenceModel textInputPreferenceModel = new TextInputPreferenceModel();
        textInputPreferenceModel.setPage_title("Sample Page Title");

        List<String> hints = new ArrayList<>();
        hints.add("hint1");
        hints.add("hint2");
        textInputPreferenceModel.setHints(hints);

        // Combine the models
        preferencesModel.setSurveyPreferenceModel(surveyPreferenceModel);
        preferencesModel.setTextInputPreferenceModel(textInputPreferenceModel);

        preferencesModel.setInstituteEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        mDatabaseReference.child(FirebaseAuth.getInstance().getUid())
                .setValue(preferencesModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully registered on realtime database");
                        getConfigValues();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "failed to add preference object", e);
                    }
                });
    }

    private void getConfigValues(){

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                PreferencesModel preferencesModel = dataSnapshot.getValue(PreferencesModel.class);
                Log.d(TAG, "Successfully obtained the preference object");

                // Store preference object in shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.REMOTE_CONFIG_PREFERENCE), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String config_string = gson.toJson(preferencesModel);
                Log.d(TAG, config_string);
                editor.putString(getString(R.string.CONFIGURATION_PREFERENCE_KEY), config_string);
                editor.putString("key", "testvalue");
                editor.commit();


                moveToSignupActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void moveToSignupActivity(){

        Log.d(TAG, "sign in and pref download complete, moving to stage acitivity");
        View sharedView = mImageView;
        String transition_name = mImageView.getTransitionName();

        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedView, transition_name);

        // TODO: Integrating Welcome fragment in stage activity
        Intent intent = new Intent(this, StageActivity.class); // Currently the naming scheme is screwed
        // Clearing the flags for animation sake
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // TODO: Fix the class naming scheme
        startActivity(intent, activityOptionsCompat.toBundle());

        // TODO: fix the activity finish animation for facilitating clean shared element view transition
        //supportFinishAfterTransition();
    }


    private void test_registerInstitutionForRemoteConfig(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        MasterWorkflow masterWorkflow = VisitUtils.getDefaultMasterWorkflow(this);

        mDatabaseReference.child(FirebaseAuth.getInstance().getUid())
                .setValue(masterWorkflow)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully registered masterworkflow on realtime database");
                        test_getConfigValues();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "failed to add masterworkflow object", e);
                    }
                });
    }


    private void test_getConfigValues(){
        Log.d(TAG, "getting config values");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                MasterWorkflow masterWorkflow = dataSnapshot.getValue(MasterWorkflow.class);
                Log.d(TAG, "Successfully obtained the masterworkflow object");
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Log.d(TAG, "children " + child.getKey());
                }

                DataSnapshot child_map = dataSnapshot.child(dataSnapshot.getChildren().iterator().next().getKey());
                // Now we can have multiple workflows within a map so iterate over each workflow and substitute for the order of screens
                for(DataSnapshot workflow: child_map.getChildren()){
                    Log.d(TAG, "workflow name = " + workflow.getKey());

                    // TODO: Find a way to remove the order_of_screens hardcoded value maybe via remoteconfig
                    DataSnapshot order_of_screens = workflow.child("order_of_screens");

                    ArrayList<Preference> orderOfScreensList = new ArrayList<>();
                    for(DataSnapshot screen : order_of_screens.getChildren()){
                        Log.d(TAG, "screen key = " + screen.getKey());

                        /*// for testing only
                        for(DataSnapshot ds : screen.getChildren()){
                            Log.d(TAG, "screen attrib = " + ds.getKey());
                        }*/


                        String type = screen.child(getString(R.string.class_type_firebase_pref)).getValue(String.class);
                        Log.d(TAG, "screen wipe = " + type);

                        if(getString(R.string.CLASS_TEXTINPUT).equals(type)){
                            Log.d(TAG, "got the text input class");
                            TextInputPreferenceModel textInputPreferenceModel = screen.getValue(TextInputPreferenceModel.class);
                            Log.d(TAG, "textclass = " + textInputPreferenceModel.getPage_title());
                            orderOfScreensList.add(textInputPreferenceModel);
                        }
                        else if(getString(R.string.CLASS_SURVEYINPUT).equals(type)){
                            Log.d(TAG, "got the survey input class");
                            SurveyPreferenceModel surveyPreferenceModel = screen.getValue(SurveyPreferenceModel.class);
                            Log.d(TAG, "survey class = " + surveyPreferenceModel.getSurvey_title());
                            orderOfScreensList.add(surveyPreferenceModel);
                        }
                        else if(getString(R.string.CLASS_CAMERA).equals(type)){
                            Log.d(TAG, "got the camera class");
                            CameraPreference cameraPreference = screen.getValue(CameraPreference.class);
                            Log.d(TAG, "survey class = " + cameraPreference.getCamera_hint_text());
                            orderOfScreensList.add(cameraPreference);
                        }
                        else if(getString(R.string.CLASS_THANKYOU).equals(type)){
                            Log.d(TAG, "got the thank you class");
                            ThankYouPreference thankYouPreference = screen.getValue(ThankYouPreference.class);
                            Log.d(TAG, "survey class = " +thankYouPreference.getThank_you_text());
                            orderOfScreensList.add(thankYouPreference);
                        }
                    }
                    masterWorkflow.getWorkflows_map().get(workflow.getKey()).setOrder_of_screens(orderOfScreensList);
                }
                // Store preference object in shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_FILE_MASTERWORKFLOW), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = GsonUtils.getGsonParser();
                String config_string = gson.toJson(masterWorkflow);
                Log.d(TAG, config_string);
                editor.putString(getString(R.string.PREF_KEY_MASTERWORKFLOW), config_string);
                editor.putString("key", "testvalue");
                editor.commit();


                //seeJsonObject(masterWorkflow);
                moveToSignupActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
