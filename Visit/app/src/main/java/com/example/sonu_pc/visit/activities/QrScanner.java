package com.example.sonu_pc.visit.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.example.sonu_pc.visit.R;

public class QrScanner extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener{

    private static final String TAG = QrScanner.class.getSimpleName();

    private static final int CAMERA_REQUEST_CODE = 111;
    private TextView resultTextView;
    private QRCodeReaderView qrCodeReaderView;
    private Button frontCamBtn, backCamBtn;
    ToneGenerator toneGen1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_sacnner);

        toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        resultTextView = (TextView) findViewById(R.id.textView10);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        /*frontCamBtn = (Button) findViewById(R.id.buttonFrontCam);
        backCamBtn = (Button) findViewById(R.id.buttonBackCam);
        frontCamBtn.setOnClickListener(this);
        backCamBtn.setOnClickListener(this);*/
    }

    /*private void setCameraParams(){

        qrCodeReaderView.setOnQRCodeReadListener(null);
        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        qrCodeReaderView.setOnQRCodeReadListener(this);
    }*/

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        resultTextView.setText(text);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        }

        qrCodeReaderView.startCamera();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the job
                    qrCodeReaderView.startCamera();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    /*@Override
    public void onClick(View v) {
        if(v == frontCamBtn){
            Log.d(TAG, "setFrontCamera Selected");
            qrCodeReaderView.stopCamera();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    setCameraParams();
                    qrCodeReaderView.setFrontCamera();
                    qrCodeReaderView.startCamera();
                }
            }, 1000);

        }
        if(v == backCamBtn){
            Log.d(TAG, "setBackCamera Selected");
            qrCodeReaderView.stopCamera();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    setCameraParams();
                    qrCodeReaderView.setBackCamera();
                    qrCodeReaderView.startCamera();
                }
            }, 1000);
        }
    }*/
}
