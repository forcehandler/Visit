package com.example.sonu_pc.visit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
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

import com.example.sonu_pc.visit.FragmentCancelListener;
import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.model.data_model.SurveyModel;
import com.example.sonu_pc.visit.model.preference_model.SurveyPreferenceModel;
import com.example.sonu_pc.visit.utils.GsonUtils;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SurveyFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private static final String TAG = SurveyFragment.class.getSimpleName();

    private ImageView mBrandLogo, mCancel;
    private EditText mEditText1, mEditText2, mEditText3, mEditText4;
    private TextView mTextViewTitle;
    private Button mButtonNext;

    private List<EditText> mEditTexts;

    private SurveyPreferenceModel mSurveyPreferenceModel;

    // Map for storing survey ques, ans

    final List<Pair<String, String>> survey_answers = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PREF_OBJ_JSON = "pref_obj_json";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mPrefObjJson;
    private String mParam2;

    private OnSurveyInteractionListener mSurveyListener;
    private FragmentCancelListener mCancelListener;

    private SurveyModel surveyModel = new SurveyModel();

    public SurveyFragment() {
        // Required empty public constructor
    }

    public static SurveyFragment newInstance(String param1, String param2) {
        SurveyFragment fragment = new SurveyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PREF_OBJ_JSON, param1);
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
            mPrefObjJson = getArguments().getString(ARG_PREF_OBJ_JSON);
            mParam2 = getArguments().getString(ARG_PARAM2);

            mSurveyPreferenceModel = getSurveyPreferenceModel(mPrefObjJson);
            Log.d(TAG, "survey pref obj = " + mSurveyPreferenceModel.getSurvey_title());
            Log.d(TAG, "survey pref obj = " + mSurveyPreferenceModel.getWipe());
        }
        mEditTexts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey, container, false);

        mBrandLogo = view.findViewById(R.id.iv_brand_logo);
        setBrandLogo();
        mEditText1 = view.findViewById(R.id.editText1);
        mEditText2 = view.findViewById(R.id.editText2);
        mEditText3 = view.findViewById(R.id.editText3);
        mEditText4 = view.findViewById(R.id.editText4);

        mCancel = view.findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);

        mTextViewTitle = view.findViewById(R.id.ratingQues1);

        mEditTexts.add(mEditText1);
        mEditTexts.add(mEditText2);
        mEditTexts.add(mEditText3);
        mEditTexts.add(mEditText4);

        mTextViewTitle.setText(mSurveyPreferenceModel.getSurvey_title());

        // Remove the unnecessary edit texts
        for (int i = mSurveyPreferenceModel.getSurvey_item_name().size(); i <= 3; i++) {
            mEditTexts.get(i).setVisibility(View.GONE);
        }

        mEditText1.setOnClickListener(this);
        mEditText2.setOnClickListener(this);
        mEditText3.setOnClickListener(this);
        mEditText4.setOnClickListener(this);
        mButtonNext = view.findViewById(R.id.btn_next);
        mButtonNext.setOnClickListener(this);

        // Set the hint text for visible edit texts
        for (int i = 0; i < mSurveyPreferenceModel.getSurvey_item_name().size(); i++) {
            mEditTexts.get(i).setHint(mSurveyPreferenceModel.getSurvey_item_name().get(i));
            mEditTexts.get(i).setOnTouchListener(this);
        }

        return view;
    }

    private void setBrandLogo() {
        File file = new File(getActivity().getFilesDir().getAbsolutePath(), "brand_logo.png");
        Uri uri = Uri.fromFile(file);
        if (file.exists()) {
            mBrandLogo.setImageURI(uri);
        }
    }

    private SurveyPreferenceModel getSurveyPreferenceModel(String json) {
        Gson gson = GsonUtils.getGsonParser();
        SurveyPreferenceModel surveyPreferenceModel = gson.fromJson(json, SurveyPreferenceModel.class);
        return surveyPreferenceModel;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FragmentCancelListener) {
            mCancelListener = (FragmentCancelListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentCancelListener");
        }

        if (context instanceof SurveyFragment.OnSurveyInteractionListener) {
            mSurveyListener = (SurveyFragment.OnSurveyInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSurveyInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSurveyListener = null;
    }

    @Override
    public void onClick(View view) {

        if (view == mButtonNext) {
            if (isEverythingAllRight()) {
                if (mSurveyListener != null) {
                    String item1 = mEditText1.getText().toString();
                    String item2 = mEditText2.getText().toString();
                    String item3 = mEditText3.getText().toString();
                    Log.d(TAG, item1 + ", " + item2 + ", " + item3);

                    Log.d(TAG, "survey answers = " + survey_answers.toString());
                    surveyModel.setSurvey_results(survey_answers);
                    mSurveyListener.onSurveyInteraction(surveyModel);
                }

            }
        }
        if(view == mCancel){
            if(mCancelListener != null){
                mCancelListener.onCancelPressed();
            }
        }
    }

    private void createOptionsDialog(final int i) {
        ArrayList<String> optionsList = mSurveyPreferenceModel.getSurvey_item_options().get(i);
        final String[] options = optionsList.toArray(new String[optionsList.size()]);

        final String survey_item_name = mSurveyPreferenceModel.getSurvey_item_name().get(i);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(survey_item_name);

        builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                Pair<String, String> pair = new Pair<>(survey_item_name, options[j]);
                survey_answers.add(pair);
                Log.d(TAG, "option selected = " + options[j]);
                if (options[j].toLowerCase().equals("other") || options[j].toLowerCase().equals("others")) {
                    othersOptionSelected(i);
                } else {
                    mEditTexts.get(i).setText(options[j]);
                }
                dialogInterface.dismiss();
            }
        }).setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                Log.d(TAG, "option selected");
                if (TextUtils.isEmpty(mEditTexts.get(i).getText().toString())) {
                    Pair<String, String> pair = new Pair<>(survey_item_name, options[0]);
                    survey_answers.add(pair);
                    mEditTexts.get(i).setText(options[0]);
                }
            }
        });


        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void othersOptionSelected(int i) {
        Log.d(TAG, "other option selected");
        mEditTexts.get(i).performClick();
    }

    public boolean isEverythingAllRight() {

        boolean isGood = true;

        for (int i = 0; i < mSurveyPreferenceModel.getSurvey_item_name().size(); i++) {
            EditText et = mEditTexts.get(i);
            String input = et.getText().toString();
            if (TextUtils.isEmpty(input)) {
                isGood = false;
                et.setError("Please select " + et.getHint());
            }
        }
        return isGood;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
            switch (view.getId()) {
                case R.id.editText1:
                    createOptionsDialog(0);
                    Log.d(TAG, "focussed = " + 0);
                    break;
                case R.id.editText2:
                    createOptionsDialog(1);
                    Log.d(TAG, "focussed = " + 1);
                    break;
                case R.id.editText3:
                    createOptionsDialog(2);
                    Log.d(TAG, "focussed = " + 2);
                    break;
                case R.id.editText4:
                    createOptionsDialog(3);
                    Log.d(TAG, "focussed = " + 3);
                    break;
            }
        }
        return true;
    }

    public ImageView getSharedImageView() {
        return mBrandLogo;
    }

    public interface OnSurveyInteractionListener {
        void onSurveyInteraction(SurveyModel surveyModel);
    }
}
