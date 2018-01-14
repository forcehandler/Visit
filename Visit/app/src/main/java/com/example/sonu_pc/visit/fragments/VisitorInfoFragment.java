package com.example.sonu_pc.visit.fragments;

import android.content.Context;
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
import android.widget.Toast;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.model.PreferencesModel;
import com.example.sonu_pc.visit.model.SurveyPreferenceModel;
import com.example.sonu_pc.visit.model.TextInputModel;
import com.example.sonu_pc.visit.model.TextInputPreferenceModel;
import com.example.sonu_pc.visit.utils.VisitUtils;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VisitorInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VisitorInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisitorInfoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = VisitorInfoFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText mEditText1, mEditText2, mEditText3, mEditText4;
    private TextView mTextViewTitle;
    private Button mButtonNext;

    private List<EditText> mEditTexts;

    private TextInputPreferenceModel mTextInputPreferenceModel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private OnVisitorInteractionListener mVisitorListener;

    public VisitorInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VisitorInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VisitorInfoFragment newInstance(String param1, String param2) {
        VisitorInfoFragment fragment = new VisitorInfoFragment();
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

        PreferencesModel preferencesModel = VisitUtils.getPreferences(getActivity());
        mTextInputPreferenceModel = preferencesModel.getTextInputPreferenceModel();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_visitor_info, container, false);
        mEditText1 = view.findViewById(R.id.editText1);
        mEditText2 = view.findViewById(R.id.editText2);
        mEditText3 = view.findViewById(R.id.editText3);
        mEditText4 = view.findViewById(R.id.editText4);

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
        }
        mButtonNext = view.findViewById(R.id.btn_next);
        mButtonNext.setOnClickListener(this);
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
            if (mListener != null) {

                mListener.onFragmentInteraction(1, 1);
                if (mVisitorListener != null) {
                    String string_name = mEditText1.getText().toString();
                    String string_company = mEditText2.getText().toString();
                    String string_phone = mEditText3.getText().toString();
                    Log.d(TAG, string_name + ", " + string_company + ", " + string_phone);
                    mVisitorListener.onVisitorInteraction(string_name, string_company, string_phone);

                    TextInputModel textInputModel = new TextInputModel();
                    Map<String, String> text_data = new HashMap<>();
                    for(int i = 0; i < mTextInputPreferenceModel.getHints().size(); i++){
                        text_data.put(mTextInputPreferenceModel.getHints().get(i), mEditTexts.get(i).getText().toString());
                    }
                    textInputModel.setText_input_data(text_data);
                    mVisitorListener.onTextInputInteraction(textInputModel);
                }

            }

        }

    }

    public boolean isEverythingAllRight(){
        Log.d(TAG, "isEverythingAllRight()");
        String string_name = mEditText1.getText().toString();
        String string_company = mEditText2.getText().toString();
        String string_phone = mEditText3.getText().toString();
        if(string_name.isEmpty() || string_company.isEmpty() || string_phone.isEmpty()){
            Toast.makeText(getActivity(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
    public interface OnVisitorInteractionListener {
        // TODO: Update argument type and name
        void onVisitorInteraction(String name, String company, String phoneNo);
        void onTextInputInteraction(TextInputModel textInputModel);
    }
}
