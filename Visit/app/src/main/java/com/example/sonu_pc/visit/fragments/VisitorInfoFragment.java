package com.example.sonu_pc.visit.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.SpeechRecognitionHelperActivity;
import com.example.sonu_pc.visit.model.data_model.TextInputModel;
import com.example.sonu_pc.visit.model.preference_model.TextInputPreferenceModel;
import com.example.sonu_pc.visit.utils.GsonUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VisitorInfoFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = VisitorInfoFragment.class.getSimpleName();


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PREF_OBJ_JSON = "pref_obj_json";
    private static final String ARG_PARAM2 = "param2";

    private EditText mEditText1, mEditText2, mEditText3, mEditText4;
    private TextView mTextViewTitle;
    private Button mButtonNext;
    private ImageView microphone;

    private EditText mFocusedEditText;

    private List<EditText> mEditTexts;

    private TextInputPreferenceModel mTextInputPreferenceModel;

    private String mPrefObjJson;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private OnVisitorInteractionListener mVisitorListener;

    private static final int REQ_CODE_SPEECH_INPUT = 111;

    public VisitorInfoFragment() {
        // Required empty public constructor
    }


    public static VisitorInfoFragment newInstance(String param1, String param2) {
        VisitorInfoFragment fragment = new VisitorInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PREF_OBJ_JSON, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG, "onCreate()");
            mPrefObjJson = getArguments().getString(ARG_PREF_OBJ_JSON);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mTextInputPreferenceModel = (TextInputPreferenceModel) getTextInputPreferenceModelFromJson(mPrefObjJson);
            Log.d(TAG, "text pref obj = " +  mTextInputPreferenceModel.getPage_title());
            Log.d(TAG, "text pref obj = " +  mTextInputPreferenceModel.getWipe());
            Log.d(TAG, "text pref obj = " +  mTextInputPreferenceModel.getHints());
        }
        mEditTexts = new ArrayList<>();
    }

    private TextInputPreferenceModel getTextInputPreferenceModelFromJson(String json){
        Gson gson = GsonUtils.getGsonParser();
        TextInputPreferenceModel textInputPreferenceModel = gson.fromJson(json, TextInputPreferenceModel.class);
        return textInputPreferenceModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //TODO: testing the workflow model
       /* PreferencesModel preferencesModel = VisitUtils.getPreferences(getActivity());
        mTextInputPreferenceModel = preferencesModel.getTextInputPreferenceModel();*/

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_visitor_info, container, false);
        mEditText1 = view.findViewById(R.id.editText1);
        mEditText2 = view.findViewById(R.id.editText2);
        mEditText3 = view.findViewById(R.id.editText3);
        mEditText4 = view.findViewById(R.id.editText4);
        microphone = view.findViewById(R.id.image_mic);

        mFocusedEditText = mEditText1;

        mTextViewTitle = view.findViewById(R.id.textView1);

        mEditTexts.add(mEditText1);
        mEditTexts.add(mEditText2);
        mEditTexts.add(mEditText3);
        mEditTexts.add(mEditText4);

        mTextViewTitle.setText(mTextInputPreferenceModel.getPage_title());
        // Remove the unnecessary edit texts
        for(int i = mTextInputPreferenceModel.getHints().size(); i <= 3; i++){
            mEditTexts.get(i).setVisibility(View.GONE);
        }

        for(int i = 0;  i < mTextInputPreferenceModel.getHints().size(); i++){
            mEditTexts.get(i).setHint(mTextInputPreferenceModel.getHints().get(i));
            mEditTexts.get(i).setOnTouchListener(this);
        }
        mButtonNext = view.findViewById(R.id.btn_next);
        mButtonNext.setOnClickListener(this);

        microphone.setOnClickListener(this);

        Log.d(TAG, "onCreateView()");
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
        if (context instanceof OnVisitorInteractionListener) {
            mVisitorListener = (OnVisitorInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnVisitorInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if(v == mButtonNext){
            Toast.makeText(getActivity(), "Next Button Pressed", Toast.LENGTH_SHORT).show();
            if(isEverythingAllRight()) {
                if (mListener != null) {

                    if (mVisitorListener != null) {
                        String string_name = mEditText1.getText().toString();
                        String string_company = mEditText2.getText().toString();
                        String string_phone = mEditText3.getText().toString();
                        Log.d(TAG, string_name + ", " + string_company + ", " + string_phone);
                        mVisitorListener.onVisitorInteraction(string_name, string_company, string_phone);

                        TextInputModel textInputModel = new TextInputModel();
                        Map<String, String> text_data = new HashMap<>();
                        for (int i = 0; i < mTextInputPreferenceModel.getHints().size(); i++) {
                            text_data.put(mTextInputPreferenceModel.getHints().get(i), mEditTexts.get(i).getText().toString());
                        }
                        textInputModel.setText_input_data(text_data);
                        mVisitorListener.onTextInputInteraction(textInputModel);
                    }
                    // First send out the info and then call for change of fragment
                    mListener.onFragmentInteraction(1, 1);
                }
            }

        }
        if(v == microphone){
            Log.v(TAG, "mic pressed");
            getTextFromSpeech();
        }

    }

    private void getTextFromSpeech(){
        if(mFocusedEditText != null){
            String hint = mFocusedEditText.getHint().toString();
            Intent intent = new Intent(getActivity(), SpeechRecognitionHelperActivity.class);
            intent.putExtra(getString(R.string.speech_dialog_title), hint);
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_CODE_SPEECH_INPUT:
                if(data != null) {
                    String result_text = data.getStringExtra(getString(R.string.TEXT_FROM_SPEECH));
                    Log.d(TAG, "speech result = result_tag");
                    mFocusedEditText.setText(result_text);
                    moveFocusToNextEditText();
                }
                break;
        }
    }

    private void moveFocusToNextEditText(){
        if(mFocusedEditText != null) {
            int focusId = mFocusedEditText.getId();
            switch (focusId){
                case R.id.editText1:
                    if(mEditText2.getVisibility() == View.VISIBLE){
                        mEditText2.requestFocus();
                        mFocusedEditText = mEditText2;
                    }
                    break;
                case R.id.editText2:
                    if(mEditText3.getVisibility() == View.VISIBLE){
                        mEditText3.requestFocus();
                        mFocusedEditText = mEditText3;
                    }
                    break;
                case R.id.editText3:
                    if(mEditText4.getVisibility() == View.VISIBLE){
                        mEditText4.requestFocus();
                        mFocusedEditText = mEditText4;
                    }
                    break;
                default:
                    mButtonNext.requestFocus();
                    break;
            }
        }
    }

    public boolean isEverythingAllRight(){

        boolean isGood = true;
        for(int i = 0;  i < mTextInputPreferenceModel.getHints().size(); i++){EditText et = mEditTexts.get(i);
           String input = et.getText().toString();
           if(TextUtils.isEmpty(input)){
               isGood = false;
               et.setError("Please enter " + et.getHint());
           }
        }
        return isGood;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(MotionEvent.ACTION_UP  == motionEvent.getAction()){
            switch (view.getId()){
                case R.id.editText1:
                    mFocusedEditText = mEditText1;
                    Log.d(TAG, "focussed = " + 1);
                    break;
                case R.id.editText2:
                    mFocusedEditText = mEditText2;
                    Log.d(TAG, "focussed = " + 2);
                    break;
                case R.id.editText3:
                    mFocusedEditText = mEditText3;
                    Log.d(TAG, "focussed = " + 3);
                    break;
                case R.id.editText4:
                    mFocusedEditText = mEditText4;
                    Log.d(TAG, "focussed = " + 4);
                    break;
            }
        }
        return false;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(int direction, int stageNo);
    }
    public interface OnVisitorInteractionListener {

        //TODO: get rid of the onVisitorInteraction based on the Coupon model
        void onVisitorInteraction(String name, String company, String phoneNo);
        void onTextInputInteraction(TextInputModel textInputModel);
    }
}
