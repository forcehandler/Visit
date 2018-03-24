package com.example.sonu_pc.visit.fragments;


import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.model.preference_model.TextInputPreferenceModel;
import com.example.sonu_pc.visit.model.preference_model.ThankYouPreference;
import com.example.sonu_pc.visit.utils.GsonUtils;
import com.google.gson.Gson;

import java.io.File;


public class ThankYouFragment extends Fragment {

    private static final String TAG = ThankYouFragment.class.getSimpleName();

    private static final String ARG_PREF_OBJ_JSON = "pref_obj_json";
    private String mPrefObjJson;

    private ImageView mBrandLogo;
    private TextView mThankYouTextView;

    private ThankYouPreference mThankYouPreference;

    private OnThankYouFragmentInteractionListener mListener;

    public ThankYouFragment() {
        // Required empty public constructor
    }

    public static ThankYouFragment newInstance(String param1) {
        ThankYouFragment fragment = new ThankYouFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PREF_OBJ_JSON, param1);
        fragment.setArguments(args);
        return fragment;
    }

    private ThankYouPreference getThankYouPreferenceFromJson(String json){
        Gson gson = GsonUtils.getGsonParser();
        ThankYouPreference mThankYouPreference = gson.fromJson(json, ThankYouPreference.class);
        return mThankYouPreference;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }

        if (getArguments() != null) {
            Log.d(TAG, "onCreate()");
            mPrefObjJson = getArguments().getString(ARG_PREF_OBJ_JSON);
            mThankYouPreference = getThankYouPreferenceFromJson(mPrefObjJson);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thank_you, container, false);

        mBrandLogo = view.findViewById(R.id.iv_brand_logo);
        setBrandLogo();
        mThankYouTextView = view.findViewById(R.id.textView_thank_you);
        mThankYouTextView.setText(mThankYouPreference.getThank_you_text());

        // Tell the main activity that
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mListener != null){
                    mListener.OnThankYouFragmentInteraction();
                }
            }
        }, 2000); // 2 sec delay
        return view;
    }

    private void setBrandLogo(){
        File file = new File(getActivity().getFilesDir().getAbsolutePath(), "brand_logo.png");
        Uri uri = Uri.fromFile(file);
        if(file.exists()){
            mBrandLogo.setImageURI(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FaceIdFragment.OnFragmentInteractionListener) {
            mListener = (ThankYouFragment.OnThankYouFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    public ImageView getSharedImageView(){
        return mBrandLogo;
    }

    public interface OnThankYouFragmentInteractionListener{
        void OnThankYouFragmentInteraction();
        void onFragmentInteraction(int direction, int stageNo);
    }


}
