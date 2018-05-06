package com.example.sonu_pc.visit.fragments;


import android.animation.ArgbEvaluator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.adapter.RatingsAdapter;
import com.example.sonu_pc.visit.model.data_model.RatingModel;
import com.example.sonu_pc.visit.model.preference_model.RatingPreferenceModel;
import com.example.sonu_pc.visit.utils.GsonUtils;
import com.google.gson.Gson;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RatingFragment extends Fragment implements RatingsAdapter.ClickListener, DiscreteScrollView.OnItemChangedListener<RatingsAdapter.MyViewHolder>, DiscreteScrollView.ScrollListener<RatingsAdapter.MyViewHolder> {

    private static final String TAG = RatingFragment.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PREF_OBJ_JSON = "pref_obj_json";

    private Button submitBtn;
    private DiscreteScrollView scrollView;
    private ImageView mBrandLogo;

    private ArgbEvaluator evaluator;
    private int currentOverlayColor;
    private int overlayColor;

    // Rating Pref object
    private RatingPreferenceModel mRatingPreference;

    // Store the ratings
    private Map<String, String> mRatingMap;

    // Listener object
    private RatingFragmentInterface mListener;

    public RatingFragment() {
        // Required empty public constructor
    }


    public static RatingFragment newInstance(String param1) {
        RatingFragment fragment = new RatingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PREF_OBJ_JSON, param1);
        fragment.setArguments(args);
        return fragment;
    }

    private RatingPreferenceModel getRatingPreferenceFromJson(String json){
        Gson gson = GsonUtils.getGsonParser();
        return gson.fromJson(json, RatingPreferenceModel.class);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof RatingFragmentInterface){
            mListener = (RatingFragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement RatingFragment Interface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        // Get the rating data from json
        if(getArguments() != null){
            String mPrefObjectJson;
            mPrefObjectJson = getArguments().getString(ARG_PREF_OBJ_JSON);
            mRatingPreference = getRatingPreferenceFromJson(mPrefObjectJson);
        }

        mRatingMap = new HashMap<>();
        evaluator = new ArgbEvaluator();
        currentOverlayColor = ContextCompat.getColor(getContext(), R.color.galleryCurrentItemOverlay);
        overlayColor = ContextCompat.getColor(getContext(), R.color.galleryItemOverlay);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        scrollView = view.findViewById(R.id.picker);
        mBrandLogo = view.findViewById(R.id.brand_logo);
        initUI();
        return view;
    }


    private void initUI(){

        setBrandLogo();
        RatingsAdapter adapter = new RatingsAdapter(getContext(), mRatingPreference.getQuestions(), this);
        scrollView.setAdapter(adapter);
        scrollView.setOffscreenItems(0);
        scrollView.setOverScrollEnabled(true);
        /*scrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                *//*.setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one*//*
                .build());*/
        scrollView.addScrollListener(this);
        scrollView.addOnItemChangedListener(this);
        scrollView.scrollToPosition(0);
        scrollView.setItemTransitionTimeMillis(100);

    }

    private void setBrandLogo(){
        File file = new File(getActivity().getFilesDir().getAbsolutePath(), "brand_logo.png");
        Uri uri = Uri.fromFile(file);
        if(file.exists()){
            mBrandLogo.setImageURI(uri);
        }
    }

    @Override
    public void onRatingClicked(int smiley) {
        Log.d(TAG, "ques: " + scrollView.getCurrentItem() + " selected smiley: " + smiley);
        mRatingMap.put(mRatingPreference.getQuestions().get(scrollView.getCurrentItem()), smiley+"");
    }

    @Override
    public void onNextClicked() {
        int currentItemIndex = scrollView.getCurrentItem();
        Log.d(TAG, "clicked next on: " + currentItemIndex);
        if(currentItemIndex + 1 < mRatingPreference.getQuestions().size()) {
            scrollView.smoothScrollToPosition(currentItemIndex + 1);
        }
        else{
            RatingModel ratingModel = new RatingModel();
            ratingModel.setRating_answers(mRatingMap);
            mListener.onRatingSubmit(ratingModel);
        }
    }


    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RatingsAdapter.MyViewHolder currentHolder, @Nullable RatingsAdapter.MyViewHolder newCurrent) {
        Log.d(TAG, "onScroll() position: " + scrollPosition + " currentPos: " + currentPosition + " newPos: " + newPosition);
        if(currentHolder != null && newCurrent != null){
            float position = Math.abs(scrollPosition);
            currentHolder.setOverlayColor(interpolate(position, currentOverlayColor, overlayColor));
            newCurrent.setOverlayColor(interpolate(position, overlayColor, currentOverlayColor));
        }
    }

    @Override
    public void onCurrentItemChanged(@Nullable RatingsAdapter.MyViewHolder viewHolder, int adapterPosition) {
        if(viewHolder != null){
            viewHolder.setOverlayColor(currentOverlayColor);
        }
    }

    private int interpolate(float fraction, int c1, int c2) {
        return (int) evaluator.evaluate(fraction, c1, c2);
    }


    public interface RatingFragmentInterface{
        void onRatingSubmit(RatingModel ratingModel);
    }

}
