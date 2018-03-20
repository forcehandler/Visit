package com.example.sonu_pc.visit.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.example.sonu_pc.visit.R;
import com.example.sonu_pc.visit.SignOutItem;
import com.example.sonu_pc.visit.fragments.dummy.DummyContent;
import com.example.sonu_pc.visit.fragments.dummy.DummyContent.DummyItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static android.support.v7.widget.RecyclerView.HORIZONTAL;
import static android.support.v7.widget.RecyclerView.VERTICAL;


public class SignOutFragment extends Fragment implements MyItemRecyclerViewAdapter.OnListFragmentInteractionListener{

    private static final String TAG = SignOutFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    private RecyclerView recyclerView;
    private SearchView searchView;
    // Wokflow to update with the visitor details
    private String workflow_name;
    private MyItemRecyclerViewAdapter adapter;

    // Store all the names
    private List<SignOutItem> itemsCopy;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SignOutFragment() {
    }

    private static final Comparator<SignOutItem> ID_COMPARATOR = new Comparator<SignOutItem>() {
        @Override
        public int compare(SignOutItem a, SignOutItem b) {
            return a.id.compareTo(b.id);
        }
    };

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SignOutFragment newInstance(int columnCount) {
        SignOutFragment fragment = new SignOutFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        setHasOptionsMenu(true);
        itemsCopy = new ArrayList<>();
        adapter = new MyItemRecyclerViewAdapter(getActivity(), this, ID_COMPARATOR);
        getDataFromFirestore();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.listRv);
        Context context = view.getContext();

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        recyclerView.setAdapter(adapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Searching submit: " + query);
                adapter.edit().removeAll().add(filter(query)).commit();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "Searching query change: " + newText);
                adapter.edit().removeAll().add(filter(newText)).commit();
                return true;
            }
        });
        return view;
    }

    private List<SignOutItem> filter(String query){
        List<SignOutItem> filterItems = new ArrayList<>();
        for(SignOutItem item : itemsCopy){
            //Log.d(TAG, "matching with: " + item.content);
            if((item.name.toLowerCase()).contains(query.toLowerCase()))
            {
                filterItems.add(item);
                Log.d(TAG, "matched with: " + item.name);
            }
        }
        return filterItems;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


    private void getDataFromFirestore(){

        CollectionReference workflowCollectionRef;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        workflowCollectionRef = db.collection(getString(R.string.collection_ref_institutes))
                .document(FirebaseAuth.getInstance().getUid()).collection(getString(R.string.collection_ref_workflows));


        workflowCollectionRef.whereEqualTo(getString(R.string.KEY_WORKFLOW_DATA_IS_WORKFLOW_SIGNOUT), true)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "obtained the workflows intended for signout");
                    for(DocumentSnapshot document : task.getResult()){
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        // get the data from this workflow
                        getDataFromSignOutWorkflow(document.getId());
                        workflow_name = document.getId();
                        break;      // WARNING: Considering only one workflow with signout!!!
                    }
                }
            }
        });

    }

    private void getDataFromSignOutWorkflow(String workflow_name){
        // get the list of, {id, name, signIn time}  of the users which haven't signed off in the workflow
        CollectionReference workflowCollectionRef;
        CollectionReference visitorsCollectionRef;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        workflowCollectionRef = db.collection(getString(R.string.collection_ref_institutes))
                .document(FirebaseAuth.getInstance().getUid()).collection(getString(R.string.collection_ref_workflows));
        visitorsCollectionRef = workflowCollectionRef.document(workflow_name).collection(getString(R.string.collection_ref_visitors));

        visitorsCollectionRef.whereEqualTo(getString(R.string.KEY_WORKFLOW_PREF_IS_SIGNEDOUT), "0")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<SignOutItem> items = new ArrayList<>();
                    for(DocumentSnapshot document : task.getResult()){
                        Log.d(TAG, "Id: " + document.getId() + "=> " + document.getData());
                        // TODO: Get the list of the items in a list of person objects and give it to the adapter
                        SignOutItem item = new SignOutItem(document.getId(), document.getData().get("name").toString(),
                                document.getData().get("signInTime").toString());
                        items.add(item);
                    }
                    itemsCopy.addAll(items);
                    if(recyclerView != null) {
                        Log.d(TAG, "Recycler view is not null, setting the adapter with items");
                        /*adapter = new MyItemRecyclerViewAdapter(items, SignOutFragment.this);*/
                        adapter.edit().add(items).commit();
                        //recyclerView.setAdapter(adapter);
                    }
                    else{
                        Log.d(TAG, "recycler view is null");
                    }
                }
            }
        });
    }

    @Override
    public void onListFragmentInteraction(final SignOutItem item) {
        Log.d(TAG, item.name + " clicked!");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(item.name);
        builder.setMessage(item.signInTime);
        builder.setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                signOutVisitor(item);
            }
        });
        builder.create().show();
    }

    private void signOutVisitor(final SignOutItem item){
        if(!TextUtils.isEmpty(workflow_name)){
            CollectionReference workflowCollectionRef;
            CollectionReference visitorsCollectionRef;

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            workflowCollectionRef = db.collection(getString(R.string.collection_ref_institutes))
                    .document(FirebaseAuth.getInstance().getUid()).collection(getString(R.string.collection_ref_workflows));
            visitorsCollectionRef = workflowCollectionRef.document(workflow_name)
                    .collection(getString(R.string.collection_ref_visitors));
            DocumentReference visitorRef = visitorsCollectionRef.document(item.id);

            visitorRef.update(getString(R.string.KEY_WORKFLOW_PREF_IS_SIGNEDOUT), "1", "signOutTime", System.currentTimeMillis()+"")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "Successfully Updated visitor: " + item.id + " signout status");
                            // Move to the main screen
                            getActivity().finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Failed to update visitor: " + item.id + " sign out status");
                }
            });

        }
        else{
            Log.e(TAG, "Error: workflow_name to upload to is empty while signing out visitor");
        }
    }

}
