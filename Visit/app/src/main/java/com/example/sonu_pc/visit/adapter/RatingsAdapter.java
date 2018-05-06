package com.example.sonu_pc.visit.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sonu_pc.visit.R;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

import java.util.List;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.MyViewHolder>{

    private static final String TAG = RatingsAdapter.class.getSimpleName();

    public Context context;
    public List<String> items;

    private ClickListener clickListener;
    @NonNull
    @Override
    public RatingsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder()");

        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final RatingsAdapter.MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() position: " + position);

        final String title = items.get(position);
        (holder).bind(title);
        (holder).view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked: " + holder.getAdapterPosition(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }


    @Override
    public int getItemCount() {

        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textView;
        public SmileRating smileRating;
        public Button nextButton;
        public View overlay;

        public View view;
        public MyViewHolder(View view){
            super(view);
            this.view = view;
            textView = view.findViewById(R.id.textView1);
            smileRating = view.findViewById(R.id.smile_rating);
            nextButton = view.findViewById(R.id.next_btn);
            overlay = itemView.findViewById(R.id.overlay);

            nextButton.setOnClickListener(this);
            smileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
                @Override
                public void onSmileySelected(int smiley, boolean reselected) {
                    clickListener.onRatingClicked(smiley);
                }
            });
        }

        public void bind(String title){
            Log.d(TAG+".MyViewHolder", "bind()");
            textView.setText(title);
            int index = this.getAdapterPosition();
            if(index == items.size()-1){
                nextButton.setText("Submit");
            }
        }

        public void setOverlayColor(@ColorInt int color){
            overlay.setBackgroundColor(color);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.next_btn:
                    clickListener.onNextClicked();
                    break;
            }
        }
    }




    public RatingsAdapter(Context context, List<String> items, ClickListener listener) {
        this.context = context;
        this.items = items;
        this.clickListener = listener;
    }

    public interface ClickListener {
        void onRatingClicked(int smiley);
        void onNextClicked();

    }
}
