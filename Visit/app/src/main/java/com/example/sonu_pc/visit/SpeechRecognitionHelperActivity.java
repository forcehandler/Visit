package com.example.sonu_pc.visit;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechRecognitionHelperActivity extends Activity implements TextToSpeech.OnInitListener{

    private static final String TAG = SpeechRecognitionHelperActivity.class.getSimpleName();

    private static final int REQ_CODE_SPEECH_INPUT = 121;
    private static final String REQ_CODE_TEXT_UTTERANCE = "Speech_recognition_helper_voice";
    private TextToSpeech textToSpeech;

    private String voice_input_hint_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        textToSpeech = new TextToSpeech(this, this);

        Intent intent = getIntent();
        voice_input_hint_text = intent.getStringExtra(getString(R.string.speech_dialog_title));
    }


    private void promptSpeechInput(String title) {
        Log.v(TAG, "speech recognition started");

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                title);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "speech result obtained");

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_SPEECH_INPUT){
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //text.setText(result.get(0));
                Log.v(TAG, "result = " + result.get(0));
                Intent resultIntent = new Intent();
                resultIntent.putExtra(getString(R.string.TEXT_FROM_SPEECH), result.get(0));
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut(voice_input_hint_text);
                promptSpeechInput(voice_input_hint_text);               // When TTS is ready, ask for start voice input
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut(String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, REQ_CODE_TEXT_UTTERANCE);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
