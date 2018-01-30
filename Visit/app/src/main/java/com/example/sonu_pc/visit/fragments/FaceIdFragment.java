package com.example.sonu_pc.visit.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.sonu_pc.visit.R;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import jp.wasabeef.picasso.transformations.GrayscaleTransformation;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FaceIdFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FaceIdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FaceIdFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = FaceIdFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VISITOR_NAME = "visitor_name";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton mImageButtonCamera;
    private CameraView mCameraView;

    private Bitmap mBitmapFacePhotoColor ;
    private Bitmap mBitmapFacePhotoBnw ;
    // TODO: Rename and change types of parameters
    private String visitor_name;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private OnFacePhotoTakenListener mPhotoListener;

    public FaceIdFragment() {
        // Required empty public constructor
        Log.d(TAG, "FaceIdFragment Constructor()");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FaceIdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FaceIdFragment newInstance(String param1, String param2) {
        FaceIdFragment fragment = new FaceIdFragment();
        Bundle args = new Bundle();
        args.putString(VISITOR_NAME, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            visitor_name = getArguments().getString(VISITOR_NAME);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_face_id, container, false);
        mImageButtonCamera = (ImageButton) view.findViewById(R.id.imageButton_camera);
        mCameraView = (CameraView) view.findViewById(R.id.camera);
        mCameraView.addCameraListener(cameraListener);
        mImageButtonCamera.setOnClickListener(this);
        Log.d(TAG, "onCreateView()");
        return view;
    }

    CameraListener cameraListener = new CameraListener() {
        @Override
        public void onPictureTaken(byte[] picture) {
            Log.i(TAG, "picture taken");
            // convert the byte array to a bitmap
            mBitmapFacePhotoColor = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            imageBnwAndCropTransform(mBitmapFacePhotoColor);
            //the bnw image will be received in the target
            //saveImage(photoBitmap);
        }
    };

    private void moveToNext(){
        if(mListener != null){
            mListener.onFragmentInteraction(1, 4);
        }
    }

    // Obtain Bnw cropped picture from picasso
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.d(TAG, "onBitmapLoaded(), obtained the bnw image");
            mBitmapFacePhotoBnw = bitmap;

            // send the images to the Activity
            if(mPhotoListener != null){
                mPhotoListener.onFacePhotoTaken(mBitmapFacePhotoColor, changeBitmapContrastBrightness(mBitmapFacePhotoBnw,2,-50));
            }
            //move to the next fragment
            moveToNext();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.e(TAG, "onBitmapFailed() while obtaining Bnw Photo");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private void imageBnwAndCropTransform(Bitmap photo) {

        Picasso.with(getActivity()).load(getImageUri(getActivity(), photo)).transform(new GrayscaleTransformation())
                .transform(new CropSquareTransformation()).resize(200,200).centerCrop().into(target);
        // The processed image is now received by the target.

    }

    /**
     *
     * @param bmp input bitmap
     * @param contrast 0..10 1 is default
     * @param brightness -255..255 0 is default
     * @return new bitmap
     */
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    //not a good hack, but picasso needs the image uri and not the bitmap itself

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
        if (context instanceof OnFacePhotoTakenListener) {
            mPhotoListener = (OnFacePhotoTakenListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFacePhotoTakenListener");
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
            case R.id.imageButton_camera:
                Toast.makeText(getActivity(), "Click!", Toast.LENGTH_SHORT).show();
                mCameraView.capturePicture();
                //TODO: move to the next fragment only when the image capture is complete
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        isStoragePermissionGranted();
        //TODO: handle the case when the permission is not granted and notify the user of the rationale
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


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // TODO: check request code
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
        else{
            Log.v(TAG,"Permission: "+permissions[0]+ "was not granted");
        }
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
        // TODO: Update argument wipe and name
        void onFragmentInteraction(int direction, int stageNo);
    }

    // TODO: Transfer the photos to the Activity for passing it to the printer module and uploading to firebase
    public interface OnFacePhotoTakenListener{
        void onFacePhotoTaken(Bitmap colorPhoto, Bitmap bnwPhoto);
    }
}
