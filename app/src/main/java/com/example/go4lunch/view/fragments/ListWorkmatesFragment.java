package com.example.go4lunch.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.view_model.ViewModelGo4Lunch;
import com.example.go4lunch.view_model.factory.ViewModelFactoryGo4Lunch;
import com.example.go4lunch.view_model.injection.Injection;
import com.example.go4lunch.view_model.repositories.UserFirebaseRepository;
import com.example.go4lunch.view.adapters.ListWorkmatesAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListWorkmatesFragment extends Fragment {

    @BindView(R.id.fragment_list_workmates_recycler_view)
    RecyclerView recyclerView;

    private ViewModelGo4Lunch viewModelGo4Lunch;
    private List<User> usersList;
    private ListWorkmatesAdapter adapter;


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
        this.configViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_workmates, container, false);
        ButterKnife.bind(this, v);
        //configRecyclerView();
        return v;

    }

    ////////////////////////////////////////// VIEW MODEL ///////////////////////////////////////////

    private void configViewModel()
    {
        ViewModelFactoryGo4Lunch viewModelFactoryGo4Lunch = Injection.viewModelFactoryGo4Lunch();
        viewModelGo4Lunch = ViewModelProviders.of(this, viewModelFactoryGo4Lunch).get(ViewModelGo4Lunch.class);
        this.getUserList();
    }

    private void getUserList()
    {
        this.viewModelGo4Lunch.getUsersListMutableLiveData().observe(this, userList ->
        {
            this.usersList = userList;
            adapter.notifyDataSetChanged();
        });
    }

    /**
     * Generate options for the FirestoreRecycler adapter with a query
     * @param query
     * @return
     */
    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query)
    {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    /*private void configRecyclerView()
    {
        ListWorkmatesAdapter adapter = new ListWorkmatesAdapter(generateOptionsForAdapter(test.usersListMutableLiveData), Glide.with(this),
                getActivity());

        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }*/
}

