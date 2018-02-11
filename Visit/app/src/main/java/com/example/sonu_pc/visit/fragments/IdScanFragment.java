package com.example.sonu_pc.visit.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.model.data_model.CameraModel;
import com.example.sonu_pc.visit.model.preference_model.CameraPreference;
import com.example.sonu_pc.visit.utils.GsonUtils;
import com.google.gson.Gson;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IdScanFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IdScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IdScanFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = IdScanFragment.class.getSimpleName();

    private static final String ARG_PREF_OBJ_JSON = "pref_obj_json";
    private static final String VISITOR_NAME = "visitor_name";

    private String visitor_name;
    private String mPrefObjJson;

    private ImageButton mImageButtonCamera;
    private CameraView mCameraView;
    private TextView mCameraHint;

    private Bitmap mBitmapIdPhotoColor ;

    private CameraPreference mCameraPreference;

    private OnFragmentInteractionListener mListener;
    private OnIdPhotoTakenListener mIdListener;

    public IdScanFragment() {}

    public static IdScanFragment newInstance(String param1, String param2) {
        Log.d(TAG, "IdScanFragment constructor");

        IdScanFragment fragment = new IdScanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PREF_OBJ_JSON, param1);
        args.putString(VISITOR_NAME, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //[Lifecycle Callbacks]
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG, "onCreate()");
            visitor_name = getArguments().getString(VISITOR_NAME);
            mPrefObjJson = getArguments().getString(ARG_PREF_OBJ_JSON);

            mCameraPreference = getCameraPreferenceModelFromJson(mPrefObjJson);
            Log.d(TAG, "camera pref json = " + mPrefObjJson);
            Log.d(TAG, "camera hint title = " + mCameraPreference.getCamera_hint_text());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_id_scan, container, false);
        mImageButtonCamera = (ImageButton) view.findViewById(R.id.imageButton_camera);
        mCameraView = (CameraView) view.findViewById(R.id.camera);
        mCameraHint = view.findViewById(R.id.tv_camera_hint);

        mCameraHint.setText(mCameraPreference.getCamera_hint_text());
        mCameraView.addCameraListener(cameraListener);
        mImageButtonCamera.setOnClickListener(this);
        return  view;
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
        if (context instanceof OnIdPhotoTakenListener) {
            mIdListener = (OnIdPhotoTakenListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnIdPhotoTakenListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCameraView.destroy();
    }

    //[Registered Callbacks]
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.imageButton_camera:
                Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();
                mCameraView.capturePicture();
                //TODO: move to the next fragment only when the image capture is complete
                break;
        }
    }

    CameraListener cameraListener = new CameraListener() {
        @Override
        public void onPictureTaken(byte[] picture) {
            Log.i(TAG, "picture taken");
            // convert the byte array to a bitmap
            mBitmapIdPhotoColor = BitmapFactory.decodeByteArray(picture, 0, picture.length);

            sendCameraModel(mBitmapIdPhotoColor);

        }
    };


   //[Utility methods]
    private CameraPreference getCameraPreferenceModelFromJson(String json){
        Log.d(TAG, "getCameraPreferenceModel");
        Gson gson = GsonUtils.getGsonParser();
        CameraPreference cameraPreference = gson.fromJson(json, CameraPreference.class);
        return cameraPreference;
    }

    private void sendCameraModel(Bitmap photo){
        if(mIdListener != null){
            Uri photoUri = saveImageToInternalStorage(mBitmapIdPhotoColor, visitor_name);
            mIdListener.onIdPhotoTaken(mBitmapIdPhotoColor);

            CameraModel cameraModel = new CameraModel();
            Pair<String, Uri> pair = new Pair<>(mCameraPreference.getCamera_hint_text(), photoUri);
            cameraModel.setCameraKeyUriPair(pair);
            mIdListener.onPhotoTaken(cameraModel);
        }

        moveToNext();
    }

    private void moveToNext(){
        if(mListener != null){
            mListener.onFragmentInteraction(1, 5);
        }
    }


    private Uri saveImageToInternalStorage(Bitmap bitmap, String filename){

        // File represents internal directory of the app
        File file = new File(getActivity().getFilesDir(), filename);

        Uri uri = Uri.fromFile(file);
        Log.d(TAG, "URI from file: " + uri);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        FileOutputStream fos;
        try{
            fos = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(bytes.toByteArray());
            fos.close();
        }
        catch (Exception e){
            Log.e(TAG, "Exception while writing file: " + filename);
            e.printStackTrace();
        }
        return uri;
    }


    //[Interfaces]
    public interface OnFragmentInteractionListener {
        // TODO: Update argument wipe and name
        void onFragmentInteraction(int direction, int stageNo);
    }

    // TODO: Transfer the photos to the Activity for passing it to the printer module and uploading to firebase
    public interface OnIdPhotoTakenListener{
        void onIdPhotoTaken(Bitmap IdPhoto);
        void onPhotoTaken(CameraModel cameraModel);
    }
}
