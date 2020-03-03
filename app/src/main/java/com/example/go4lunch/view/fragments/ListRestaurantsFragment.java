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
import com.example.go4lunch.model.GenerateTests;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.view.adapters.ListRestaurantsAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListRestaurantsFragment extends Fragment {

    private List<Restaurant> restaurants;
    private ListRestaurantsAdapter adapter;

    @BindView(R.id.fragment_list_restaurants_recycler_view)
    RecyclerView recyclerView;


    public ListRestaurantsFragment() {
        // Required empty public constructor
    }

    public static ListRestaurantsFragment newInstance() {
        ListRestaurantsFragment fragment = new ListRestaurantsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_restaurants, container, false);
        ButterKnife.bind(this, v);
        configRecyclerView();
        return v;

    }

    private void configRecyclerView()
    {
        this.restaurants = GenerateTests.getRestaurants();
        this.adapter = new ListRestaurantsAdapter(restaurants, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

}
