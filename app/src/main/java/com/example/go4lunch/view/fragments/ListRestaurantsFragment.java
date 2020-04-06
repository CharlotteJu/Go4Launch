package com.example.go4lunch.view.fragments;


import android.Manifest;
import android.content.Intent;
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
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.api.RestaurantStreams;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.example.go4lunch.view.adapters.ListRestaurantsAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class ListRestaurantsFragment extends Fragment implements OnClickListener{

    private List<Restaurant> restaurants;
    private ListRestaurantsAdapter adapter;
    private static final int REQUEST_CODE = 12;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_restaurants, container, false);
        restaurants = new ArrayList<>();
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
        this.adapter = new ListRestaurantsAdapter(restaurants, Glide.with(this), this, getActivity(), currentLocation);
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    /**
     * Configure the List<Restaurant> while checking the Access Permission
     */
    private void configListRestaurants()
    {
        if (ActivityCompat.checkSelfPermission(
                Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                stream(currentLocation.getLatitude(), currentLocation.getLongitude(), 500);
            }
        });


    }




    @OnClick(R.id.fragment_list_restaurants_near_me_fab)
    void triProximity ()
    {
        Collections.sort(restaurants, (o1, o2) -> {
            Integer restau1 = o1.getDistanceCurrentUser();
            Integer restau2 = o2.getDistanceCurrentUser();

            return restau1.compareTo(restau2);

        });
        this.adapter.notifyDataSetChanged();

    }



    @OnClick(R.id.fragment_list_restaurants_rating_fab)
    void triRate ()
    {
        Collections.sort(restaurants, (o1, o2) -> {
            Double restau1 = o1.getRating();
            Double restau2 = o2.getRating();

            return restau1.compareTo(restau2);
        });

        Collections.reverse(restaurants);

        this.adapter.notifyDataSetChanged();
    }


    @OnClick(R.id.fragment_list_restaurants_name_fab)
    void triName()
    {
        Collections.sort(restaurants, (o1, o2) -> {

            String restau1 = o1.getName();
            String restau2 = o2.getName();


            return restau1.compareTo(restau2);
        });

        this.adapter.notifyDataSetChanged();
    }



    ////////////////////////////////////////// RXJAVA ///////////////////////////////////////////

    /**
     * Recover the List<Restaurant> with the HTTP Request
     * @param lat double with latitude of the current User
     * @param lng double with longitude of the current User
     * @param radius double to define the distance around the current User
     * @return a List<Restaurant>
     */
    private void stream(double lat, double lng, int radius)
    {
       String key = BuildConfig.google_maps_key;

       this.restaurants.clear();

       this.disposable = RestaurantStreams.streamFetchRestaurantInList(lat, lng, radius, key).subscribeWith(new DisposableObserver<List<Restaurant>>() {
           @Override
            public void onNext(List<Restaurant> restaurantList) {

               restaurants = restaurantList;
               configRecyclerView();
            }

            @Override
            public void onError(Throwable e) {

               String error = "error";


            }

            @Override
            public void onComplete() {

            }
        });
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
    public void onDestroy() {
        super.onDestroy();
        this.unsubscribe();
    }

    @Override
    public void onClickListener(int position)
    {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra("placeId", restaurants.get(position).getPlaceId());
        startActivity(intent);
    }
}
