package com.example.sonu_pc.visit.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.transition.TransitionInflater;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.sonu_pc.visit.activities.QrScanner;
import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.activities.SignOutActivity;
import com.example.sonu_pc.visit.model.preference_model.MasterWorkflow;
import com.example.sonu_pc.visit.model.preference_model.PreferencesModel;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WelcomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = WelcomeFragment.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageView mBrandLogo;
    private Button mButton_1, mButton_2, mButton_3;
    private Button mButtonQrSignIn;

    private List<Button> buttonList;

    private ImageView mImageViewBrandLogo;

    private MasterWorkflow masterWorkflow;

    Map<Integer, String> button_workflow_map;

    private String mParam1;
    private String mParam2;

    private OnWelcomeFragmentInteractionListener mListener;

    public WelcomeFragment() {
        // Required empty public constructor
    }


    public static WelcomeFragment newInstance(String param1, String param2) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        initMasterWorkflow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        buttonList = new ArrayList<>();

        mBrandLogo = view.findViewById(R.id.iv_brand_logo);
        setBrandLogo();
        mButton_1 = view.findViewById(R.id.button1);
        mButton_2 = view.findViewById(R.id.button2);
        mButton_3 = view.findViewById(R.id.button3);

        buttonList.add(mButton_1);
        buttonList.add(mButton_2);
        buttonList.add(mButton_3);

        mButtonQrSignIn = view.findViewById(R.id.btn_qr_signin);

        mButtonQrSignIn.setOnClickListener(this);
        mImageViewBrandLogo = view.findViewById(R.id.iv_brand_logo);

        button_workflow_map = new HashMap<>();
        int button_counter = 0;
        Map<String, PreferencesModel> map =  masterWorkflow.getWorkflows_map();

        //TODO: Check if the mapping order stays the same for button and workflow
        for(String key : map.keySet()){
            if(button_counter < 3) {
                Button btn = buttonList.get(button_counter++);
                Log.d(TAG, "Adding workflow button: " + key);
                btn.setVisibility(View.VISIBLE);
                btn.setText(key);
                btn.setOnClickListener(this);
                button_workflow_map.put(btn.getId(), key);
            }
            try {
                if (map.get(key).isWorkflowForSignOut()) {
                    mButtonQrSignIn.setVisibility(View.VISIBLE);
                    mButtonQrSignIn.setText("Sign Out");
                }
            }catch (Exception e){
                Log.e(TAG, "Error while checking for wf's isWorkflowForSignOut function");
                Log.e(TAG, e.toString());
            }
        }

        //mButtonQrSignIn.setOnClickListener(this);
        return view;
    }


    private void setBrandLogo(){
        File file = new File(getActivity().getFilesDir().getAbsolutePath(), "brand_logo.png");
        Uri uri = Uri.fromFile(file);
        if(file.exists()){
            mBrandLogo.setImageURI(uri);
        }
    }

    private void initMasterWorkflow(){
        SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.PREF_FILE_MASTERWORKFLOW), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String workflow_json = preferences.getString(getString(R.string.PREF_KEY_MASTERWORKFLOW), "NOPREF");
        if(workflow_json == "NOPREF"){
            Log.e(TAG, "Could not fetch the workflow json from sharedpreferences");
        }
        else{
            masterWorkflow = gson.fromJson(workflow_json, MasterWorkflow.class);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWelcomeFragmentInteractionListener) {
            mListener = (OnWelcomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int id = v.getId();

        Log.d(TAG, "onClick: " + id);
        switch (id){

            case R.id.button1:
            case R.id.button2:
            case R.id.button3:
                mListener.onWelcomeFragmentInteraction(button_workflow_map.get(id));
                break;

            case R.id.btn_qr_signin:
                intent = new Intent(getActivity(), SignOutActivity.class);
                startActivity(intent);
                break;
        }
    }

    public ImageView getSharedImageView(){
        return mBrandLogo;
    }

    public interface OnWelcomeFragmentInteractionListener {
        void onWelcomeFragmentInteraction(String selected_workflow_key);
    }
}
