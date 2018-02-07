package com.example.sonu_pc.visit.activities;

/**
 * Created by sonupc on 30-01-2018.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

import android.util.Log;

import com.example.sonu_pc.visit.R;


public class voiceRecognitionTest extends Activity
{

    private TextView mText;
    private SpeechRecognizer sr;

    private TextToSpeech textToSpeech;

    private static final String REQ_CODE_TEXT_UTTERANCE = "Speech_recognition_helper_voice";

    private static final String TAG = "MyStt3Activity";

    int i = 0;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition_test);

        //Button speakButton = (Button) findViewById(R.id.btn_speak);
        mText = (TextView) findViewById(R.id.textView1);
        //speakButton.setOnClickListener(this);
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());

        //textToSpeech = new TextToSpeech(this, this);
        //textToSpeech.setOnUtteranceProgressListener(new ttsListener());

    }

    /*@Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut("hello world!");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut(String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, REQ_CODE_TEXT_UTTERANCE);
    }*/



    /*class ttsListener extends UtteranceProgressListener{
        @Override
        public void onStart(final String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    appendLog("onStart " + s);
                }
            });
        }

        @Override
        public void onDone(final String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            appendLog("onDone " + s);
                        }
                    });
                }
            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //startRecognition();
                    speakOut("another stuff " + i++);
                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    appendLog("starting recognition");
                }
            });

        }

        @Override
        public void onError(String s) {
            appendLog("onError " + s);
        }
    }*/

    class listener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
            appendLog("onReadyForSpeech");
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
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }
            appendLog("results: "+String.valueOf(data.size()));
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
            appendLog("onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
            appendLog("onEvent " + eventType);
        }
    }

    public void startRecognition() {
        //if (v.getId() == R.id.btn_speak)
        {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
            if(sr.isRecognitionAvailable(this)){
                Log.d(TAG, "recognition is available");
            }
            else{
                Log.d(TAG, "Recognition is NOT available");
            }
            sr.startListening(intent);
            Log.i(TAG,"started listening");
        }
    }

    private void appendLog(String s){
        mText.setText(mText.getText().toString() + "\n" + s);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        Log.d(TAG, "onDestroy");
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

}