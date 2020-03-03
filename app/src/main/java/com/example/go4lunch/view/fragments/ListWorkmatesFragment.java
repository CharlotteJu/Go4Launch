package com.example.go4lunch.view.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.GenerateTests;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.view.adapters.ListRestaurantsAdapter;
import com.example.go4lunch.view.adapters.ListWorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListWorkmatesFragment extends Fragment {


    private List<User> users;
    private ListWorkmatesAdapter adapter;

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

    private void configRecyclerView()
    {
        this.users = GenerateTests.getUsers();
        this.adapter = new ListWorkmatesAdapter(users, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

}

