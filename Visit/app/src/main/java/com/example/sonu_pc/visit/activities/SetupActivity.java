package com.example.sonu_pc.visit.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.model.preference_model.CameraPreference;
import com.example.sonu_pc.visit.model.preference_model.MasterWorkflow;
import com.example.sonu_pc.visit.model.preference_model.Preference;
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

public class SetupActivity extends AppCompatActivity {

    private static final String TAG = SetupActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 124;

    private DatabaseReference mDatabaseReference;

    private ConstraintLayout mConstraintLayout;
    private ProgressBar mProgressBar;
    private ImageView mImageView;

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 111;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 121;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 131;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_setup);

        mConstraintLayout = findViewById(R.id.constraint_container_setup);
        mProgressBar = findViewById(R.id.progress_bar);
        mImageView = findViewById(R.id.brand_logo);

        if(areAllPermissionsSatisfied()){
            Log.d(TAG, "all permissions are satisfied now");
            startSigninSequence();
        }
        else{
            checkForPermissions();
        }

        // start sign in sequence after all the permissions are satisfied

    }

    private void startSigninSequence(){

        Log.d(TAG, "starting sign in sequence");
        FirebaseApp.initializeApp(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            /*Snackbar.make(mConstraintLayout, "Already Signed in", Snackbar.LENGTH_SHORT)
                    .show();*/

            // check if we already have the config values

            // TODO: for testing only, added always on registration and commented get config vavlues
            //registerInstitutionForRemoteConfig();
            if(!haveConfigValues()){
                getConfigValues();
            }
            else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moveToStageActivity();
                    }
                }, 2000);
            }

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

    private boolean haveConfigValues(){
        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE_MASTERWORKFLOW), Context.MODE_PRIVATE);
        String config_json = preferences.getString(getString(R.string.PREF_KEY_MASTERWORKFLOW), "NOPREF");
        if(config_json.equals("NOPREF")){
            return false;
        }
        return true;
    }

    private void startTtsService(){
        Log.d(TAG, "start TtsService");
        Intent serviceIntent = new Intent(this, TextToSpeechService.class);
        this.startService(serviceIntent);
    }


    private void moveToStageActivity(){

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


    private void registerInstitutionForRemoteConfig(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        MasterWorkflow masterWorkflow = VisitUtils.getDefaultMasterWorkflow(this);

        mDatabaseReference.child(FirebaseAuth.getInstance().getUid())
                .setValue(masterWorkflow)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Successfully registered masterworkflow on realtime database");
                        getConfigValues();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "failed to add masterworkflow object", e);
                    }
                });
    }

    private void checkUserAlreadyRegistered(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    MasterWorkflow masterWorkflow = dataSnapshot.getValue(MasterWorkflow.class);
                    if(masterWorkflow != null){
                        Log.d(TAG, "user already registered, fetching old values!!");
                        getConfigValues();
                    }
                    else {
                        Log.d(TAG, "user not already registered, registering....");
                        registerInstitutionForRemoteConfig();
                    }
                }
                catch (Exception e){
                    Log.d(TAG, "NEW USER, registering the user on realtime database with default config");
                    //TODO: Check this one out!!
                    registerInstitutionForRemoteConfig();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getConfigValues(){
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

                DataSnapshot child_map = dataSnapshot.child(MasterWorkflow.WORKFLOW_MAP_KEY);  // Name of the map variable in the MasterWorkflow
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
                moveToStageActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private boolean areAllPermissionsSatisfied(){

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            return false;
        }

    }
    private void checkForPermissions(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

        }
        else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
            else {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SIGN_IN: {
                IdpResponse response = IdpResponse.fromResultIntent(data);

                // Successfully signed in
                if (resultCode == RESULT_OK) {
                    Snackbar.make(mConstraintLayout, "Successfully Signed In", Snackbar.LENGTH_SHORT)
                            .show();
                    // First time registration of institution on realtime database for remote config

                    //TODO: commenting for testing the already user exist function
                    //registerInstitutionForRemoteConfig();

                    //################################################33
                    checkUserAlreadyRegistered();

                } else {
                    // Sign in failed
                    if (response == null) {
                        // user pressed back button
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, requestCode + " granted");

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                } else {
                    Log.d(TAG, requestCode + " not granted.");
                    finish();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, requestCode + " granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);

                } else {

                    Log.d(TAG, requestCode + " not granted.");
                    finish();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, requestCode + " granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    if(areAllPermissionsSatisfied()){
                        Log.d(TAG, "areAllpermissionssatisfied check in the permission result activity");
                        startSigninSequence();
                    }
                } else {

                    Log.d(TAG, requestCode + " not granted.");
                    finish();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
