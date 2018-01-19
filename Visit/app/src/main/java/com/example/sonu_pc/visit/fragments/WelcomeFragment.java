package com.example.sonu_pc.visit.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.sonu_pc.visit.activities.QrScanner;
import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.activities.SignUpActivity;


public class WelcomeFragment extends Fragment implements View.OnClickListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button mButtonSignUp;
    private Button mButtonQrSignIn;

    private ImageView mImageViewBrandLogo;


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        mButtonSignUp = (Button) view.findViewById(R.id.btn_signup);
        mButtonQrSignIn = (Button) view.findViewById(R.id.btn_qr_signin);
        mImageViewBrandLogo = view.findViewById(R.id.imageViewLogo);

        mButtonSignUp.setOnClickListener(this);
        mButtonQrSignIn.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
        Intent intent = null;
        int id = v.getId();
        switch (id){
            case R.id.btn_signup:
                View sharedView = mImageViewBrandLogo;
                String transitionName = mImageViewBrandLogo.getTransitionName();

                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), sharedView, transitionName);
                intent = new Intent(getActivity(), SignUpActivity.class);
                startActivity(intent, activityOptionsCompat.toBundle());
                break;
            case R.id.btn_qr_signin:
                intent = new Intent(getActivity(), QrScanner.class);
                startActivity(intent);
                break;
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
