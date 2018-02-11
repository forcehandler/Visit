package com.example.sonu_pc.visit.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.activities.printer.PrinterActivity;
import com.example.sonu_pc.visit.fragments.FaceIdFragment;
import com.example.sonu_pc.visit.fragments.IdScanFragment;
import com.example.sonu_pc.visit.fragments.NonDisclosureFragment;
import com.example.sonu_pc.visit.fragments.SurveyFragment;
import com.example.sonu_pc.visit.fragments.ThankYouFragment;
import com.example.sonu_pc.visit.fragments.VisiteeInfoFragment;
import com.example.sonu_pc.visit.fragments.VisitorInfoFragment;
import com.example.sonu_pc.visit.model.CouponModel;
import com.example.sonu_pc.visit.model.data_model.CameraModel;
import com.example.sonu_pc.visit.model.data_model.DataModel;
import com.example.sonu_pc.visit.model.preference_model.MasterWorkflow;
import com.example.sonu_pc.visit.model.preference_model.PreferencesModel;
import com.example.sonu_pc.visit.model.data_model.SurveyModel;
import com.example.sonu_pc.visit.model.data_model.TextInputModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity implements VisitorInfoFragment.OnFragmentInteractionListener,
        FaceIdFragment.OnFragmentInteractionListener, NonDisclosureFragment.OnFragmentInteractionListener,
        VisiteeInfoFragment.OnFragmentInteractionListener, IdScanFragment.OnFragmentInteractionListener ,
        FaceIdFragment.OnFacePhotoTakenListener, IdScanFragment.OnIdPhotoTakenListener,
        VisitorInfoFragment.OnVisitorInteractionListener, VisiteeInfoFragment.OnVisiteeInteractionListener,
        SurveyFragment.OnSurveyInteractionListener, SurveyFragment.OnFragmentInteractionListener,
        ThankYouFragment.OnThankYouFragmentInteractionListener{

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private static ArrayList<Boolean> mStagePreferences;
    public CouponModel couponModel;

    // Replacement for coupon model;
    private DataModel mDataModel;

    private FirebaseFirestore mFirestore;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private DatabaseReference mDatabaseReference;

    private PreferencesModel preferencesModel;

    private MasterWorkflow masterWorkflow;

    private ConstraintLayout mConstraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign_up);

        // Initialize the views
        mConstraintLayout = findViewById(R.id.constraint_container_signup);

        couponModel = new CouponModel();

        // replacement for coupon model
        mDataModel = new DataModel();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if(fragment == null){
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, VisitorInfoFragment.newInstance("",""))
                    .commit();
        }

        mStagePreferences = new ArrayList<>();

        //Old settings method
        //fillStagePreferences();

        initFirestore();

        initMasterWorkflow();


        // Config values from Realtime database
        fillRemoteStagePreferences();


        //Temporary fix
        //fillStagePreferences();
    }

    private void initFirestore(){
        mFirestore = FirebaseFirestore.getInstance();

        // Enable offline
    }

    private void initMasterWorkflow(){
        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREF_FILE_MASTERWORKFLOW), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String workflow_json = preferences.getString(getString(R.string.PREF_KEY_MASTERWORKFLOW), "NOPREF");
        if(workflow_json == "NOPREF"){
            Log.e(TAG, "Could not fetch the workflow json from sharedpreferences");
        }
        else{
            masterWorkflow = gson.fromJson(workflow_json, MasterWorkflow.class);
        }
    }

    private void initRemoteConfig(){
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        fillRemoteConfigStagePreferences();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid());
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Config from Realtime Database (Intended final use)
    private void fillRemoteStagePreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.REMOTE_CONFIG_PREFERENCE), MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString(getString(R.string.CONFIGURATION_PREFERENCE_KEY), "");
        String test = sharedPreferences.getString("key", "");
        Log.d(TAG, "testpref = " + test);
        Log.d(TAG, "preference object json = " + json);
        preferencesModel = (PreferencesModel) gson.fromJson(json, PreferencesModel.class);

        boolean stage1 = preferencesModel.isStage1();
        boolean stage2 = preferencesModel.isStage2();
        boolean stage3 = preferencesModel.isStage3();
        boolean stage4 = preferencesModel.isStage4();
        boolean stage5 = preferencesModel.isStage5();
        boolean stage6 = preferencesModel.isStage6();

        mStagePreferences.add(stage1);
        mStagePreferences.add(stage2);
        mStagePreferences.add(stage3);
        mStagePreferences.add(stage4);
        mStagePreferences.add(stage5);
        mStagePreferences.add(stage6);
    }

    // Config from Remote Config
    private void fillRemoteConfigStagePreferences(){

        boolean stage1 = mFirebaseRemoteConfig.getBoolean(getString(R.string.REMOTE_STAGE1_KEY));
        boolean stage2 = mFirebaseRemoteConfig.getBoolean(getString(R.string.REMOTE_STAGE2_KEY));
        boolean stage3 = mFirebaseRemoteConfig.getBoolean(getString(R.string.REMOTE_STAGE3_KEY));
        boolean stage4 = mFirebaseRemoteConfig.getBoolean(getString(R.string.REMOTE_STAGE4_KEY));
        boolean stage5 = mFirebaseRemoteConfig.getBoolean(getString(R.string.REMOTE_STAGE5_KEY));

        mStagePreferences.add(stage1);
        mStagePreferences.add(stage2);
        mStagePreferences.add(stage3);
        mStagePreferences.add(stage4);
        mStagePreferences.add(stage5);
    }

    // Config from saved settings
    private void fillStagePreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean stage1 = sharedPreferences.getBoolean(getString(R.string.pref_key_stage1), true);
        boolean stage2 = sharedPreferences.getBoolean(getString(R.string.pref_key_stage2), true);
        boolean stage3 = sharedPreferences.getBoolean(getString(R.string.pref_key_stage3), true);
        boolean stage4 = sharedPreferences.getBoolean(getString(R.string.pref_key_stage4), true);
        boolean stage5 = sharedPreferences.getBoolean(getString(R.string.pref_key_stage5), true);

        mStagePreferences.add(stage1);
        mStagePreferences.add(stage2);
        mStagePreferences.add(stage3);
        mStagePreferences.add(stage4);
        mStagePreferences.add(stage5);

        int i = 1;
        for(boolean l : mStagePreferences){
            Log.d(TAG, "stage " + (i++) + " = " + l);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction(int direction, int stageNo) {
        Log.d(TAG, "onFragmentInteraction()");
        int moveToStageNo = -1;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (direction){
            case 0:
                Log.d(TAG, "move back");
                moveToStageNo = prevStage(stageNo);
                break;
            case 1:
                Log.d(TAG, "move ahead");
                moveToStageNo = nextStage(stageNo);
                break;

        }
        Log.d(TAG, "curr stage = " + stageNo);
        Log.d(TAG, "moving to stage = " + moveToStageNo);
        switch(moveToStageNo){
            case 1:
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.fragment_container, VisitorInfoFragment.newInstance("",""))

                        .addToBackStack(null)
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.fragment_container, SurveyFragment.newInstance("",""))

                        .addToBackStack(null)
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.fragment_container, VisiteeInfoFragment.newInstance("",""))

                        .addToBackStack(null)
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.fragment_container, FaceIdFragment.newInstance("",""))

                        .commit();
                break;
            case 5:
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.fragment_container, IdScanFragment.newInstance("",""))

                        .commit();
                break;
            case 6:
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.fragment_container, NonDisclosureFragment.newInstance(preferencesModel.getTermsAndCond(),""))

                        .commit();
                break;
            case -1:
                //printIdCard();
                //TODO: Verify the integrity of the CouponModel
                fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .replace(R.id.fragment_container, new ThankYouFragment())

                        .commit();
                //addVisitor(couponModel);

                // replacement for coupon model
                uploadDataModel();
                break;
        }
    }

    private void uploadDataModel(){
        CollectionReference visitor_reference = mFirestore.collection("institutes")
                .document(FirebaseAuth.getInstance().getUid()).collection("visitors");

        // Get the time of signUp
        String visitor_id = System.currentTimeMillis() + "";  //for now this will be the visitor uid

        visitor_reference.document(visitor_id).set(mDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showSnackbar("Successfully uploaded the data model");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar("Failed to addd the data model");
            }
        });
    }

    private void addVisitor(CouponModel couponModel){
        // Currently assuming only one institute. Will require
        CollectionReference visitors = mFirestore.collection("institutes")
                .document(FirebaseAuth.getInstance().getUid()).collection("visitors");



        // Get the time of signUp
        long signup_time = System.currentTimeMillis();

        // filename for the uid of the visitor
        String filename = couponModel.getVisitor_name() + signup_time;
        String visitorId = filename;

        couponModel.setVisitor_uid(filename); // filename is uid of customer

        // Upload the face and id images if required by the organization
        if(mStagePreferences.get(3) == true) {
            // 3 => face photo
            uploadFaceImage(couponModel, filename);
        }
        if(mStagePreferences.get(4) == true){
            // 4 => id photo
            // TODO: Do it!
            //uploadIdImage(couponModel);
        }

        visitors.document(visitorId).set(couponModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showSnackbar("Successfully added the visitor");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar("Failed to add the visitor");
            }
        });

        // Add this visitor to the list
        /*visitors.add(couponModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Snackbar.make(findViewById(android.R.id.content), "Successfully added the visitor", Snackbar.LENGTH_SHORT)
                        .show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(android.R.id.content), "Failed to add the visitor", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });*/
    }

    private String uploadFaceImage(CouponModel model, String filename){

        Log.d(TAG, "Uploading photo");

        storeFaceImage(model, filename); // store the face image with the supplied filename in internal storage

        // Get reference to the stored file
        FileInputStream inputStream;
        try {
            inputStream = openFileInput(filename);

            Bitmap bm = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 20, baos1);


            // Prepare for upload
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();

            // Store images under the reference name of institute uid
            StorageReference instituteRef = storageReference.child(FirebaseAuth.getInstance().getUid());

            // Store the image with the name of the visitor
            StorageReference faceImageRef = instituteRef.child(getString(R.string.faceId_storage_key)).child(filename);

            InputStream stream = new FileInputStream(new File(getFilesDir() + "/" + filename));
            UploadTask uploadTask = faceImageRef.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    showSnackbar("Upload Unsuccessful");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-wipe, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d(TAG, downloadUrl.toString());
                    showSnackbar("Upload successful @ url = " + downloadUrl);
                }
            });
            inputStream.close();
        } catch (Exception e){
            Log.e(TAG, "error in reading file");
            e.printStackTrace();
        }

        return filename;  // Reference to the stored image on firebase storage
    }

    private String storeFaceImage(CouponModel model, String filename){

        // store the drawable in the internal storage
        // Obtain the file reference to the image and upload it to uuid/visitorName_timeOfSignUp

        // Store the color image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        model.getVisitor_face_photo().compress(Bitmap.CompressFormat.JPEG, 10, baos);

        // Saving drawable to internal storage
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(baos.toByteArray());
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, "Error in Writing file");
            e.printStackTrace();
        }

        return filename;
    }

    private void printIdCard(){
        Log.d(TAG, "sending the model for printing");

        /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
        couponModel.getVisitor_face_photo().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();*/

        Intent intent = new Intent(this, PrinterActivity.class);
        intent.putExtra(getString(R.string.intent_key_coupon), couponModel);
        startActivity(intent);
    }

    private int prevStage(int currStageNo){
        for(int i = currStageNo - 2; i >= 0; i++){
            if(mStagePreferences.get(i) == true){
                return i+1;
            }
        }
        return -1; // go to the last stage i.e. Print the photo
    }

    private int nextStage(int currStageNo){
        for(int i = currStageNo; i < mStagePreferences.size(); i++){
            if(mStagePreferences.get(i) == true){
                return i+1;
            }
        }
        return -1; // go to the last stage i.e. Print the photo
    }


    @Override
    public void onFacePhotoTaken(Bitmap colorPhoto, Bitmap bnwPhoto) {
        // TODO: use the photos for printing and uploading to firebase
        Log.d(TAG, "obtained face photo");
        couponModel.setVisitor_face_photo(bnwPhoto);
    }

    @Override
    public void onPhotoTaken(Uri photo) {

    }

    @Override
    public void onIdPhotoTaken(Bitmap IdPhoto) {
        Log.d(TAG, "obtained id photo");
    }

    @Override
    public void onPhotoTaken(CameraModel cameraModel) {

    }


    @Override
    public void onTextInputInteraction(TextInputModel textInputModel) {
        Log.d(TAG, "obtained textinput model");

        mDataModel.setTextInputModel(textInputModel);
    }

    @Override
    public void onVisiteeInteraction(String name, String purpose, String time) {
        Log.d(TAG, "obtained visitee info");
        couponModel.setVisitee_name(name);
        //TODO: Add visitee position as well
        couponModel.setVisitee_position("CEO");
    }

    @Override
    public void onSurveyInteraction(SurveyModel surveyModel) {
        Log.d(TAG, "obtained survey info");
        couponModel.setSurveyModel(surveyModel);

        // new model, replacement for coupon model
        mDataModel.setSurveyModel(surveyModel);
    }

    @Override
    public void OnThankYouFragmentInteraction() {
        Intent intent = new Intent(this, MasterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showSnackbar(String message){
        Snackbar.make(mConstraintLayout, message, Snackbar.LENGTH_SHORT).show();
    }



}
