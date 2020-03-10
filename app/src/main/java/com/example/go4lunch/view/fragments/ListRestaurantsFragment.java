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
import com.example.go4lunch.model.RestaurantPOJO;
import com.example.go4lunch.model.api.RestaurantStreams;
import com.example.go4lunch.view.adapters.ListRestaurantsAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class ListRestaurantsFragment extends Fragment {

    private List<Restaurant> restaurants;
    private ListRestaurantsAdapter adapter;

    private Disposable disposable;

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

    ////////////////////////////////////////// RXJAVA ///////////////////////////////////////////

    private Observable<String> getObservable ()
    {
        return Observable.just("Observable");
    }

    private DisposableObserver<String> getSubsriber()
    {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void stream(double lat, double lng, int radius)
    {
        this.disposable = RestaurantStreams.streamFetchRestaurant(lat, lng, radius).subscribeWith(new DisposableObserver<List<RestaurantPOJO>>() {
            @Override
            public void onNext(List<RestaurantPOJO> restaurantPOJOS) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void unsubscribe()
    {
        if (this.disposable != null && !this.disposable.isDisposed())
        {
            this.disposable.dispose();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unsubscribe();
    }


}
