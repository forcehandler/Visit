package com.example.sonu_pc.visit.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.SignOutItem;
import com.example.sonu_pc.visit.fragments.dummy.DummyContent.DummyItem;
import com.github.wrdlbrnft.sortedlistadapter.SortedListAdapter;

import java.util.Comparator;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends SortedListAdapter<SignOutItem> {

    private static final String TAG = MyItemRecyclerViewAdapter.class.getSimpleName();

    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(Context context, OnListFragmentInteractionListener listener, Comparator<SignOutItem> comparator) {
        super(context, SignOutItem.class, comparator);
        mListener = listener;
    }


    @Override
    protected SortedListAdapter.ViewHolder<? extends SignOutItem> onCreateViewHolder(LayoutInflater layoutInflater, ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.fragment_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    protected boolean areItemsTheSame(SignOutItem signOutItem, SignOutItem t1) {
        return signOutItem.equals(t1);
    }

    @Override
    protected boolean areItemContentsTheSame(SignOutItem signOutItem, SignOutItem t1) {
        return signOutItem.name == t1.name;
    }


    public class ViewHolder extends SortedListAdapter.ViewHolder<SignOutItem>{
        public final View mView;
        //public final TextView mIdView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        protected void performBind(final SignOutItem signOutItem) {
            mContentView.setText(signOutItem.name);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(signOutItem);
                    }
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(SignOutItem item);
    }
}
