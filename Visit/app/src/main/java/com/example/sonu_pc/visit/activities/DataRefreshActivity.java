package com.example.sonu_pc.visit.activities;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.fragments.SuggestionFragment;
import com.example.sonu_pc.visit.model.data_model.SuggestionModel;
import com.example.sonu_pc.visit.model.preference_model.CameraPreference;
import com.example.sonu_pc.visit.model.preference_model.MasterWorkflow;
import com.example.sonu_pc.visit.model.preference_model.Preference;
import com.example.sonu_pc.visit.model.preference_model.RatingPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.SuggestionPreference;
import com.example.sonu_pc.visit.model.preference_model.SurveyPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.TextInputPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.ThankYouPreference;
import com.example.sonu_pc.visit.utils.GsonUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import android.support.constraint.Group;
import java.util.ArrayList;

public class DataRefreshActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = DataRefreshActivity.class.getSimpleName();

    private Button btn_refresh_logo, btn_refresh_wf;
    private ProgressBar progressBar;
    private Group group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_refresh);

        btn_refresh_logo = findViewById(R.id.btn_logo);
        btn_refresh_wf = findViewById(R.id.btn_wf);
        progressBar = findViewById(R.id.progress_bar);
        group = findViewById(R.id.group_refresh);

        btn_refresh_wf.setOnClickListener(this);
        btn_refresh_logo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_logo:
                getBrandLogo();
                break;
            case R.id.btn_wf:
                getConfigValues();
                break;
        }
    }



    private void getConfigValues(){

        showProgressBar();
        Log.d(TAG, "getting config values");
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid());
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
                        else if(getString(R.string.CLASS_RATING).equals(type)){
                            Log.d(TAG, "got the rating class");
                            RatingPreferenceModel ratingPreferenceModel = screen.getValue(RatingPreferenceModel.class);
                            //Log.d(TAG, "survey class = " + cameraPreference.getCamera_hint_text());
                            orderOfScreensList.add(ratingPreferenceModel);
                        }
                        else if(getString(R.string.CLASS_SUGGESTION).equals(type)){
                            Log.d(TAG, "got the rating class");
                            SuggestionPreference preference = screen.getValue(SuggestionPreference.class);
                            Log.d(TAG, "suggestion class = " + preference.getSuggestion_text());
                            orderOfScreensList.add(preference);
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

                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getBrandLogo(){
        showProgressBar();
        // Download the logo in a file named brand_logo
        final File file = new File(getFilesDir().getAbsolutePath(), "brand_logo.png");
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference(FirebaseAuth.getInstance().getUid() + "/Logo/brand_logo.png");

        if(file.exists()){
            Log.d(TAG, "file already exists");
            Log.d(TAG, "delete file result: " + file.delete());
        }
        else{
            storageReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "Logo download successful");
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "error downloading Logo");
                    Toast.makeText(DataRefreshActivity.this, "Error downloading Logo", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        btn_refresh_logo.setVisibility(View.GONE);
        btn_refresh_wf.setVisibility(View.GONE);
        group.setVisibility(View.GONE);
    }
}
