package com.example.sonu_pc.visit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, VisitorInfoFragment.OnFragmentInteractionListener,
        FaceIdFragment.OnFragmentInteractionListener, NonDisclosureFragment.OnFragmentInteractionListener,
        VisiteeInfoFragment.OnFragmentInteractionListener, IdScanFragment.OnFragmentInteractionListener ,
        FaceIdFragment.OnFacePhotoTakenListener, IdScanFragment.OnIdPhotoTakenListener{

    private static final String TAG = SignUpActivity.class.getSimpleName();


    private ImageButton mImageButtonRight, mImageButtonLeft;

    private static ArrayList<Boolean> mStagePreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mImageButtonRight = (ImageButton) findViewById(R.id.imageButton_right);
        mImageButtonLeft = (ImageButton) findViewById(R.id.imageButton_left);
        //mImageButtonRight.setOnClickListener(this);
        //mImageButtonLeft.setOnClickListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if(fragment == null){
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, VisitorInfoFragment.newInstance("",""))
                    .commit();
        }
        mStagePreferences = new ArrayList<>();
        fillStagePreferences();

    }

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
    public void onClick(View v) {
        int id = v.getId();
        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        switch (id){
            case(R.id.imageButton_right):
                Toast.makeText(this, "Right Button Pressed", Toast.LENGTH_SHORT).show();

                if(currFrag != null && currFrag instanceof VisitorInfoFragment){
                    Log.d(TAG, "moving ahead to face ID fragment");
                    if(((VisitorInfoFragment) currFrag).isEverythingAllRight()){
                        //TODO : Add fragments to the backstack and recover them instead of creating new ones
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, FaceIdFragment.newInstance("",""))
                                .commit();
                    }
                }
                break;
            case (R.id.imageButton_left):
                Toast.makeText(this, "Left Button Pressed", Toast.LENGTH_SHORT).show();
                if(currFrag != null && currFrag instanceof FaceIdFragment){
                    Log.d(TAG, "moving back to face ID fragment");
                    //TODO : Add fragments to the backstack and recover them instead of creating new ones
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, VisitorInfoFragment.newInstance("",""))
                            .commit();

                }
                break;
        }
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
                        .replace(R.id.fragment_container, VisitorInfoFragment.newInstance("",""))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, VisiteeInfoFragment.newInstance("",""))
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FaceIdFragment.newInstance("",""))
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, IdScanFragment.newInstance("",""))
                        .commit();
                break;
            case 5:
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, NonDisclosureFragment.newInstance("",""))
                        .commit();
                break;
        }
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
    }

    @Override
    public void onIdPhotoTaken(Bitmap IdPhoto) {

    }
}
