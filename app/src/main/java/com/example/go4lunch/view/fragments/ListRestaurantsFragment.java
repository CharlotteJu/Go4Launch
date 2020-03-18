package com.example.go4lunch.view.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.api.RestaurantStreams;
import com.example.go4lunch.view.adapters.ListRestaurantsAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class ListRestaurantsFragment extends Fragment{

    private List<Restaurant> restaurants = new ArrayList<>();
    private ListRestaurantsAdapter adapter;
    private static final int REQUEST_CODE = 12;
    private Location currentLocation;

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
        configListRestaurants();
        return v;

    }

    ////////////////////////////////////////// CONFIGURE ///////////////////////////////////////////


    /**
     * Configure the RecyclerView
     */
    private void configRecyclerView()
    {
        this.adapter = new ListRestaurantsAdapter(restaurants, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    /**
     * Configure the List<Restaurant> while checking the Access Permission
     */
    private void configListRestaurants()
    {
        if (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        }
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    restaurants = stream(currentLocation.getLatitude(), currentLocation.getLongitude(), 500);
                }
            }
        });


    }

    ////////////////////////////////////////// RXJAVA ///////////////////////////////////////////

    /**
     * Recover the List<Restaurant> with the HTTP Request
     * @param lat double with latitude of the current User
     * @param lng double with longitude of the current User
     * @param radius double to define the distance around the current User
     * @return a List<Restaurant>
     */
    private List<Restaurant> stream(double lat, double lng, int radius)
    {
        String key = getResources().getString(R.string.google_maps_key);

        /*this.disposable = RestaurantStreams.test1(lat, lng, radius, key).subscribeWith(new DisposableObserver<List<Restaurant>>() {

            @Override
            public void onNext(List<Restaurant> restaurantList)
            {
                restaurants = restaurantList;
                configRecyclerView();
            }


            @Override
            public void onError(Throwable e) { }

            @Override
            public void onComplete() { }
        });*/

       this.disposable = RestaurantStreams.streamRestaurantListFinal(lat, lng, radius, key).subscribeWith(new DisposableObserver<List<Restaurant>>() {
           @Override
            public void onNext(List<Restaurant> restaurantList) {

               restaurants = restaurantList;
               configRecyclerView();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        return restaurants;
    }

    /**
     * Unsubscribe of the HTTP Request
     */
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
