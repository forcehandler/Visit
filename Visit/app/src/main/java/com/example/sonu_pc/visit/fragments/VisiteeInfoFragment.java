package com.example.sonu_pc.visit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sonu_pc.visit.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VisiteeInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VisiteeInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisiteeInfoFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = VisiteeInfoFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText mEditTextVisiteeName, mEditTextVisitPurpose, mEditTextStayTime;
    private Button mButtonNext, mButtonPrev;
    private OnFragmentInteractionListener mListener;
    private OnVisiteeInteractionListener mVisiteeListener;

    public VisiteeInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VisiteeInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VisiteeInfoFragment newInstance(String param1, String param2) {
        VisiteeInfoFragment fragment = new VisiteeInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_visitee_info, container, false);
        mEditTextVisiteeName = (EditText) view.findViewById(R.id.editText_visitee_name);
        mEditTextVisitPurpose = (EditText) view.findViewById(R.id.editText_visit_purpose);
        mEditTextStayTime = (EditText) view.findViewById(R.id.editText_stay_time);

        mButtonNext = (Button) view.findViewById(R.id.button_next);
        mButtonPrev = (Button) view.findViewById(R.id.button_previous);
        mButtonNext.setOnClickListener(this);
        mButtonPrev.setOnClickListener(this);
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
        if (context instanceof OnVisiteeInteractionListener) {
            mVisiteeListener = (OnVisiteeInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnVisiteeInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.button_next:
                if(mListener != null){
                    mListener.onFragmentInteraction(1, 3);
                }
                if(mVisiteeListener != null && isEverythingAllRight()){
                    String string_visitee_name = mEditTextVisiteeName.getText().toString();
                    String string_purpose = mEditTextVisitPurpose.getText().toString();
                    String string_stay_time = mEditTextStayTime.getText().toString();

                    mVisiteeListener.onVisiteeInteraction(string_visitee_name, string_purpose, string_stay_time);
                }
                break;
            case R.id.button_previous:
                if(mListener != null){
                    mListener.onFragmentInteraction(0, 3);
                }
        }
    }

    public boolean isEverythingAllRight(){
        Log.d(TAG, "isEverythingAllRight()");
        String string_visitee_name = mEditTextVisiteeName.getText().toString();
        String string_purpose = mEditTextVisitPurpose.getText().toString();
        String string_stay_time = mEditTextStayTime.getText().toString();
        if(string_visitee_name == null || string_purpose == null || string_stay_time == null
                || string_visitee_name == "" || string_purpose == "" || string_stay_time == ""){
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

    public interface OnVisiteeInteractionListener {
        // TODO: Update argument type and name
        void onVisiteeInteraction(String name, String purpose, String time);
    }
}
