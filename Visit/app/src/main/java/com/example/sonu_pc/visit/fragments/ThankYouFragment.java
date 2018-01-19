package com.example.sonu_pc.visit.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sonu_pc.visit.R;


public class ThankYouFragment extends Fragment {

    private static final String TAG = ThankYouFragment.class.getSimpleName();

    private OnThankYouFragmentInteractionListener mListener;

    public ThankYouFragment() {
        // Required empty public constructor
    }

    public static FaceIdFragment newInstance() {
        FaceIdFragment fragment = new FaceIdFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_thank_you, container, false);
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

    public interface OnThankYouFragmentInteractionListener{
        void OnThankYouFragmentInteraction();
    }

}
