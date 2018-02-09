package com.example.sonu_pc.visit.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TextToSpeechService extends Service {

    private static final String TAG = TextToSpeechService.class.getSimpleName();

    private static TextToSpeech voice =null;

    public static TextToSpeech getVoice() {
        return voice;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // not supporting binding
        return null;
    }

    public TextToSpeechService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try{
            Log.d("TTSService","Text-to-speech object initializing");

            voice = new TextToSpeech(TextToSpeechService.this,new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(final int status) {
                    Log.d(TAG,"Text-to-speech object initialization complete");
                    if (status == TextToSpeech.SUCCESS) {

                        int result = voice.setLanguage(new Locale("en", "IN"));

                        if (result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e(TAG, "This Language is not supported");
                        } else {

                        }

                    } else {
                        Log.e(TAG, "Initialization Failed!");
                    }
                }
            });

        }
        catch(Exception e){
            e.printStackTrace();
        }


        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        clearTtsEngine();
        super.onDestroy();

    }

    public static void clearTtsEngine()
    {
        if(voice!=null)
        {
            voice.stop();
            voice.shutdown();
            voice = null;
        }



    }
}
