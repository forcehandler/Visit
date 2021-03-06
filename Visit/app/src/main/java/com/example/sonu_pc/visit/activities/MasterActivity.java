package com.example.sonu_pc.visit.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.fragments.WelcomeFragment;
import com.example.sonu_pc.visit.model.preference_model.MasterWorkflow;
import com.google.gson.Gson;

public class MasterActivity extends AppCompatActivity implements WelcomeFragment.OnWelcomeFragmentInteractionListener {

    private static final String TAG = MasterActivity.class.getSimpleName();

    private ConstraintLayout mConstraintLayout;
    private LinearLayout mProgressContainer;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_master);

        mConstraintLayout = findViewById(R.id.constraint_layout_master);



        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if(fragment == null){
            fragmentManager.beginTransaction().add(R.id.container_fragment, WelcomeFragment.newInstance("","")).
                    commit();
        }


    }


    @Override
    public void onWelcomeFragmentInteraction(String selected_workflow_key) {

    }
}
