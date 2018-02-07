package com.example.sonu_pc.visit;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.example.sonu_pc.visit.activities.voiceRecognitionTest;

import java.util.Locale;

public class TextToSpeechService extends Service implements TextToSpeech.OnInitListener{

    private final IBinder mBinder = new TextToSpeechBinder();


    private TextToSpeech textToSpeech;

    private static final String REQ_CODE_TEXT_UTTERANCE = "Speech_recognition_helper_voice";

    private static final String TAG = TextToSpeechService.class.getSimpleName();

    int i = 0;


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class TextToSpeechBinder extends Binder {
        TextToSpeechService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TextToSpeechService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setOnUtteranceProgressListener(new ttsListener());
    }

    class ttsListener extends UtteranceProgressListener {
        @Override
        public void onStart(final String s) {

            appendLog("onStart " + s);
        }

        @Override
        public void onDone(final String s) {

            appendLog("onDone " + s);

            //startRecognition();
            speakOut("another stuff " + i++);

            appendLog("starting recognition");

        }

        @Override
        public void onError(String s) {
            appendLog("onError " + s);
        }
    }

    @Override
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
    }

    public TextToSpeechService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void appendLog(String s){
        Log.d(TAG, s);
    }
}
