package com.example.sonu_pc.visit.activities;

import android.app.Activity;
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
import com.example.sonu_pc.visit.model.PreferencesModel;
import com.example.sonu_pc.visit.model.SurveyPreferenceModel;
import com.example.sonu_pc.visit.model.TextInputPreferenceModel;
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
        FirebaseApp.initializeApp(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            Snackbar.make(mConstraintLayout, "Already Signed in", Snackbar.LENGTH_SHORT)
                    .show();

            // Obtain the config values
            getConfigValues();
        }
        else {
            // not signed in
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder().build(),
                    RC_SIGN_IN);
        }
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
                registerInstitutionForRemoteConfig();
                // Obtain the config values
                //getConfigValues();      // Required here as well to save defaults to shared preferences
                // ^ Now called after the Institute is successfully registered
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

        View sharedView = mImageView;
        String transition_name = mImageView.getTransitionName();

        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedView, transition_name);
        Intent intent = new Intent(this, MasterActivity.class); // Currently the naming scheme is screwed
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // TODO: Fix the class naming scheme
        startActivity(intent, activityOptionsCompat.toBundle());
        finish();
    }
}
