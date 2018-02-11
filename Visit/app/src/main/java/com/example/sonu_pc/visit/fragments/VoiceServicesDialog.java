package com.example.sonu_pc.visit.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sonu_pc.visit.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnVoiceServicesDialogInteractionListener} interface
 * to handle interaction events.
 */
public class VoiceServicesDialog extends DialogFragment implements TextToSpeech.OnInitListener{

    private static final String TAG = VoiceServicesDialog.class.getSimpleName();

    private TextView tv_tts_hint, tv_stt_hint;

    private SpeechRecognizer sr;

    private OnVoiceServicesDialogInteractionListener mListener;

    private VisitorInfoFragment mVisitorInfoFragment;

    private static final String ARG_SPEECH_HINT_TEXT = "hint text";
    private String mSpeechHintText;

    public VoiceServicesDialog() {
        // Required empty public constructor
    }

    public static VoiceServicesDialog newInstance(String param1) {
        VoiceServicesDialog fragment = new VoiceServicesDialog();
        Bundle args = new Bundle();
        args.putString(ARG_SPEECH_HINT_TEXT, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Log.d(TAG, "onCreate()");
            mSpeechHintText = getArguments().getString(ARG_SPEECH_HINT_TEXT);

            sr = SpeechRecognizer.createSpeechRecognizer(getContext());
            sr.setRecognitionListener(new listener());
            mVisitorInfoFragment = (VisitorInfoFragment) getTargetFragment();
        }
        else{
            Log.e(TAG, "No Speech hint set for the dialog fragment. Initialize fragment by new instance and not by constructor");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        tv_tts_hint = view.findViewById(R.id.tv_speech_hint_text);
        tv_stt_hint = view.findViewById(R.id.tv_listening_hint);

        tv_tts_hint.setText(mSpeechHintText);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onVoiceServicesDialogInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onInit(int i) {

    }

    public interface OnVoiceServicesDialogInteractionListener {
        // TODO: Update argument type and name
        void onVoiceServicesDialogInteraction(Uri uri);
    }

    public void startRecognition() {
        //if (v.getId() == R.id.btn_speak)
        {
            final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
            if(sr.isRecognitionAvailable(getContext())){
                Log.d(TAG, "recognition is available");
            }
            else{
                Log.d(TAG, "Recognition is NOT available");
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sr.startListening(intent);
                }
            });

            Log.i(TAG,"started listening");
        }
    }


    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
            appendLog("onReadyForSpeech");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_stt_hint.setText("Start Speaking...");
                }
            });

        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
            appendLog("onBeginningOfSpeech");

        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
            appendLog("onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndofSpeech");
            appendLog("onEndofSpeech");
        }
        public void onError(int error)
        {
            //int a = sr.ERRo;

            Log.d(TAG,  "error " +  error);
            appendLog("error = " + error);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_stt_hint.setText("Could not recognize your voice, please try again");

                }
            });
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                appendLog("result " + data.get(i));
                str += data.get(i);
            }
            appendLog("results: "+String.valueOf(data.size()));
            appendLog("obtained voice to text string = " + str);
            mVisitorInfoFragment.speechDialogResult(data.get(0).toString());
            dismiss();

        }
        public void onPartialResults(Bundle partialResults)
        {
            //Log.d(TAG, "onPartialResults");
            appendLog("onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            //Log.d(TAG, "onEvent " + eventType);
            appendLog("onEvent " + eventType);
        }
    }

    private void appendLog(String s){
        Log.d(TAG, s);
    }
}
