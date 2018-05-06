package com.example.sonu_pc.visit.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.model.data_model.SuggestionModel;
import com.example.sonu_pc.visit.model.preference_model.SuggestionPreference;
import com.example.sonu_pc.visit.utils.GsonUtils;
import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class SuggestionFragment extends Fragment implements TextWatcher, View.OnClickListener {

    private static final String TAG = SuggestionFragment.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PREF_OBJ_JSON = "pref_obj_json";


    private TextInputEditText textInputEditText;
    private Button submitBtn;
    private TextView suggestionTv;
    private ImageView mBrandLogo;
    // Rating Pref object
    private SuggestionPreference mSuggestionPreference;

    // Store the ratings
    private SuggestionModel mSuggestionModel;
    private Map<String, String> mSuggestionMap;

    // Listener object
    private SuggestionFragmentInterface mListener;

    public SuggestionFragment() {}

    public static SuggestionFragment newInstance(String param1) {
        SuggestionFragment fragment = new SuggestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PREF_OBJ_JSON, param1);
        fragment.setArguments(args);
        return fragment;
    }

    private SuggestionPreference getSuggestionPreferenceFromJson(String json){
        Gson gson = GsonUtils.getGsonParser();
        return gson.fromJson(json, SuggestionPreference.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            String mPrefObjectJson;
            mPrefObjectJson = getArguments().getString(ARG_PREF_OBJ_JSON);
            mSuggestionPreference = getSuggestionPreferenceFromJson(mPrefObjectJson);
        }
        mSuggestionModel = new SuggestionModel();
        mSuggestionMap = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggestion, container, false);
        textInputEditText = view.findViewById(R.id.textInputEditText1);
        submitBtn = view.findViewById(R.id.submit_btn);
        suggestionTv = view.findViewById(R.id.tv_suggestion);
        mBrandLogo = view.findViewById(R.id.iv_brand_logo);
        setBrandLogo();
        suggestionTv.setText(mSuggestionPreference.getSuggestion_text());
        textInputEditText.addTextChangedListener(this);
        submitBtn.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SuggestionFragmentInterface) {
            mListener = (SuggestionFragmentInterface) context;
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.toString().trim().length()>0){
            setButtonTextAsNext(true);
        }else{
            setButtonTextAsNext(false);
        }
    }

    private void setButtonTextAsNext(boolean val){
        if(val){
            submitBtn.setText(getResources().getText(R.string.text_next));
        }
        else {
            submitBtn.setText(getResources().getText(R.string.text_skip));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.submit_btn:
                submitSuggestion();
                break;
        }
    }

    private void submitSuggestion(){
        String s = textInputEditText.getText().toString();
        mSuggestionMap.put(mSuggestionPreference.getSuggestion_text(), s);
        mSuggestionModel.setSuggestions_map(mSuggestionMap);
        mListener.onSuggestionSubmit(mSuggestionModel);
    }

    private void setBrandLogo(){
        File file = new File(getActivity().getFilesDir().getAbsolutePath(), "brand_logo.png");
        Uri uri = Uri.fromFile(file);
        if(file.exists()){
            mBrandLogo.setImageURI(uri);
        }
    }

    public interface SuggestionFragmentInterface {
        void onSuggestionSubmit(SuggestionModel suggestionModel);
    }
}
