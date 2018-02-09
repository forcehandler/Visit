package com.example.sonu_pc.visit.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by sonupc on 09-02-2018.
 */


public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {

    private static final String TAG = BroadcastReceiverOnBootComplete.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "Boot Receiver Fired");
            Intent serviceIntent = new Intent(context, TextToSpeechService.class);
            context.startService(serviceIntent);
        }
    }

}