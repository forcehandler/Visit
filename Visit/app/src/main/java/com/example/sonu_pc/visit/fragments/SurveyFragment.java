package com.example.sonu_pc.visit.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.model.PreferencesModel;
import com.example.sonu_pc.visit.model.SurveyModel;
import com.example.sonu_pc.visit.model.SurveyPreferenceModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SurveyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SurveyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurveyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = SurveyFragment.class.getSimpleName();


    private EditText mEditText1, mEditText2, mEditText3, mEditText4;
    private TextView mTextViewTitle;
    private Button mButtonNext;

    private List<EditText> mEditTexts;

    private SurveyPreferenceModel surveyPreferenceModel;

    // Map for storing survey ques, ans

    final Map<String, String> survey_answers = new HashMap<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private OnSurveyInteractionListener mSurveyListener;

    private SurveyModel surveyModel = new SurveyModel();
    public SurveyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SurveyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SurveyFragment newInstance(String param1, String param2) {
        SurveyFragment fragment = new SurveyFragment();
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
        mEditTexts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getActivity()
                        .getSharedPreferences(getString(R.string.REMOTE_CONFIG_PREFERENCE), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String preference_object_json = sharedPreferences.getString(getString(R.string.CONFIGURATION_PREFERENCE_KEY), "");

        PreferencesModel preferencesModel = gson.fromJson(preference_object_json, PreferencesModel.class);
        surveyPreferenceModel = preferencesModel.getSurveyPreferenceModel();

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_survey, container, false);
        mEditText1 = view.findViewById(R.id.editText1);
        mEditText2 = view.findViewById(R.id.editText2);
        mEditText3 = view.findViewById(R.id.editText3);
        mEditText4 = view.findViewById(R.id.editText4);

        mTextViewTitle = view.findViewById(R.id.textView1);

        mEditTexts.add(mEditText1);
        mEditTexts.add(mEditText2);
        mEditTexts.add(mEditText3);
        mEditTexts.add(mEditText4);

        mTextViewTitle.setText(surveyPreferenceModel.getSurvey_title());

        // Remove the unnecessary edit texts
        for(int i = surveyPreferenceModel.getSurvey_item_name().size(); i <= 3; i++){
            mEditTexts.get(i).setVisibility(View.GONE);
        }

        mEditText1.setOnClickListener(this);
        mEditText2.setOnClickListener(this);
        mEditText3.setOnClickListener(this);
        mEditText4.setOnClickListener(this);
        mButtonNext = view.findViewById(R.id.btn_next);
        mButtonNext.setOnClickListener(this);

        // Set the hint text for visible edit texts
        for(int i = 0; i < surveyPreferenceModel.getSurvey_item_name().size(); i++){
            mEditTexts.get(i).setHint(surveyPreferenceModel.getSurvey_item_name().get(i));
        }
       /* mEditText1.setHint(surveyPreferenceModel.getSurvey_item_name().get(0));
        mEditText2.setHint(surveyPreferenceModel.getSurvey_item_name().get(1));
        mEditText3.setHint(surveyPreferenceModel.getSurvey_item_name().get(2));*/

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
        mListener = null;
    }

    @Override
    public void onClick(View view) {


        if(view == mButtonNext){
            if(mListener != null){
                if(mSurveyListener != null){
                    String item1 = mEditText1.getText().toString();
                    String item2 = mEditText2.getText().toString();
                    String item3 = mEditText3.getText().toString();
                    Log.d(TAG, item1 + ", " +  item2 + ", " + item3);
                    mSurveyListener.onSurveyInteraction(surveyModel);
                }
                // First send out the info and then call for change of fragment
                mListener.onFragmentInteraction(1,2);
            }


        }


        if(view == mEditText1){

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(surveyPreferenceModel.getSurvey_item_name().get(0));

            // list of options
            ArrayList<String> optionsList = surveyPreferenceModel.getSurvey_item_options().get(0);
            final String [] options = optionsList.toArray(new String[optionsList.size()]);

            builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    survey_answers.put(surveyPreferenceModel.getSurvey_item_name().get(0), options[i]);
                    Log.d(TAG, "option selected = " + options[i]);
                    mEditText1.setText(options[i]);
                }
            });
            builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "option selected");
                }
            });
            builder.create().show();
        }
        if(view == mEditText2){
            // list of options
            ArrayList<String> optionsList = surveyPreferenceModel.getSurvey_item_options().get(1);
            final String [] options = optionsList.toArray(new String[optionsList.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(surveyPreferenceModel.getSurvey_item_name().get(1));

            builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    survey_answers.put(surveyPreferenceModel.getSurvey_item_name().get(1), options[i]);
                    Log.d(TAG, "option selected = " + options[i]);
                    mEditText2.setText(options[i]);
                }
            });
            builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "option selected");
                }
            });
            builder.create().show();
        }
        if(view == mEditText3){
            // list of options
            ArrayList<String> optionsList = surveyPreferenceModel.getSurvey_item_options().get(2);
            final String [] options = optionsList.toArray(new String[optionsList.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(surveyPreferenceModel.getSurvey_item_name().get(2));

            builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    survey_answers.put(surveyPreferenceModel.getSurvey_item_name().get(2), options[i]);
                    Log.d(TAG, "option selected = " + options[i]);
                    mEditText3.setText(options[i]);
                }
            });
            builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "option selected");
                }
            });
            builder.create().show();
        }

        if(view == mEditText4){
            // list of options
            ArrayList<String> optionsList = surveyPreferenceModel.getSurvey_item_options().get(3);
            final String [] options = optionsList.toArray(new String[optionsList.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(surveyPreferenceModel.getSurvey_item_name().get(2));

            builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    survey_answers.put(surveyPreferenceModel.getSurvey_item_name().get(3), options[i]);
                    Log.d(TAG, "option selected = " + options[i]);
                    mEditText4.setText(options[i]);
                }
            });
            builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d(TAG, "option selected");
                }
            });
            builder.create().show();
        }
        Log.d(TAG, "survey answers = " + survey_answers.toString());
        surveyModel.setSurvey_results(survey_answers);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(int direction, int stageNo);
    }
    public interface OnSurveyInteractionListener {
        // TODO: Update argument type and name
        void onSurveyInteraction(SurveyModel surveyModel);
    }
}
