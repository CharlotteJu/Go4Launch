package com.example.go4lunch.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.UserHelper;
import com.example.go4lunch.view.adapters.ListWorkmatesAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListWorkmatesFragment extends Fragment implements ListWorkmatesAdapter.Listener{


    private List<User> users;
    private ListWorkmatesAdapter adapter;
    private User currentUser;

    @BindView(R.id.fragment_list_workmates_recycler_view)
    RecyclerView recyclerView;


    public ListWorkmatesFragment() {
        // Required empty public constructor
    }

    public static ListWorkmatesFragment newInstance() {
        ListWorkmatesFragment fragment = new ListWorkmatesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_workmates, container, false);
        ButterKnife.bind(this, v);
        configRecyclerView();
        return v;

    }

    private void initUserList()
    {
        //SINGLE LISTENER
        //final DocumentSnapshot docRef = UserHelper.getCollectionUser().document()

    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query)
    {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    private void configRecyclerView()
    {
        /*UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                currentUser = documentSnapshot.toObject(User.class);
            }
        });*/
        this.adapter = new ListWorkmatesAdapter(generateOptionsForAdapter(UserHelper.getListUsers()), Glide.with(this),
                this, FirebaseAuth.getInstance().getCurrentUser().getEmail());


        //this.users = GenerateTests.getUsers();
        //this.adapter = new ListWorkmatesAdapter(users, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }


    @Override
    public void onDataChanged()
    {

    }
}

