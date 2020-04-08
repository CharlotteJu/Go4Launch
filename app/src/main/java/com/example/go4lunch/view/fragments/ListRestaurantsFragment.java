package com.example.go4lunch.view.fragments;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

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
import com.example.go4lunch.utils.StaticFields;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.example.go4lunch.view.adapters.ListRestaurantsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class ListRestaurantsFragment extends Fragment implements OnClickListener{

    private List<Restaurant> restaurantList;
    private ListRestaurantsAdapter adapter;
    //private static final int REQUEST_CODE = 12;
    private Location currentLocation;
    //private FusedLocationProviderClient fusedLocationProviderClient;
    private Disposable disposable;


    @BindView(R.id.fragment_list_restaurants_recycler_view)
    RecyclerView recyclerView;


    public ListRestaurantsFragment() {
        // Required empty public constructor
    }

    public static ListRestaurantsFragment newInstance()
    {
        ListRestaurantsFragment fragment = new ListRestaurantsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));
        this.currentLocation = StaticFields.CURRENT_LOCATION;
        this.restaurantList = StaticFields.RESTAURANTS_LIST;

        this.updateDistanceToCurrentLocation();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_restaurants, container, false);
        //restaurantList = new ArrayList<>();
        ButterKnife.bind(this, v);
        //configListRestaurants();

        this.configRecyclerView();
        return v;

    }

    ////////////////////////////////////////// CONFIGURE ///////////////////////////////////////////


    /**
     * Configure the RecyclerView
     */
    private void configRecyclerView()
    {
        this.adapter = new ListRestaurantsAdapter(restaurantList, Glide.with(this), this, getActivity(), currentLocation);
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    /**
     * Configure the List<Restaurant> while checking the Access Permission
     */
   /* private void configListRestaurants()
    {
        currentLocation = StaticFields.CURRENT_LOCATION;
        stream(currentLocation.getLatitude(), currentLocation.getLongitude(), 500);

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


    }*/

    @OnClick(R.id.fragment_list_restaurants_near_me_fab)
    void triProximity ()
    {
        Collections.sort(restaurantList, (o1, o2) -> {
            Integer restau1 = o1.getDistanceCurrentUser();
            Integer restau2 = o2.getDistanceCurrentUser();

            return restau1.compareTo(restau2);

        });
        this.adapter.notifyDataSetChanged();

    }

    @OnClick(R.id.fragment_list_restaurants_rating_fab)
    void triRate ()
    {
        Collections.sort(restaurantList, (o1, o2) -> {
            Double restau1 = o1.getRating();
            Double restau2 = o2.getRating();

            return restau1.compareTo(restau2);
        });

        Collections.reverse(restaurantList);

        this.adapter.notifyDataSetChanged();
    }


    @OnClick(R.id.fragment_list_restaurants_name_fab)
    void triName()
    {
        Collections.sort(restaurantList, (o1, o2) -> {

            String restaurant1 = o1.getName();
            String restaurant2 = o2.getName();

            return restaurant1.compareTo(restaurant2);
        });

        this.adapter.notifyDataSetChanged();
    }

    /**
     * Update the attribute DistanceCurrentUser for each restaurant
     */
    private void updateDistanceToCurrentLocation()
    {
        Location restaurantLocation = new Location("fusedLocationProvider");

        for (int i = 0; i < restaurantList.size(); i ++)
        {
            //Get the restaurant's location
            restaurantLocation.setLatitude(restaurantList.get(i).getLocation().getLat());
            restaurantLocation.setLongitude(restaurantList.get(i).getLocation().getLng());
            //Get the distance between currentLocation and restaurantLocation
            int distanceLocation = (int) currentLocation.distanceTo(restaurantLocation);

            restaurantList.get(i).setDistanceCurrentUser(distanceLocation);
        }
    }



    ////////////////////////////////////////// RXJAVA ///////////////////////////////////////////


    /*private void stream(double lat, double lng, int radius)
    {
        String key = BuildConfig.google_maps_key;

        this.restaurantList.clear();

        this.disposable = RestaurantStreams.streamFetchRestaurantInList(lat, lng, radius, key).subscribeWith(new DisposableObserver<List<Restaurant>>() {
            @Override
            public void onNext(List<Restaurant> restaurantList) {

                ListRestaurantsFragment.this.restaurantList = restaurantList;
                updateDistanceToCurrentLocation();
                configRecyclerView();
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onComplete() {}
        });
    }

    /**
     * Unsubscribe of the HTTP Request
     */
    /*private void unsubscribe()
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
    }*/

    @Override
    public void onClickListener(int position)
    {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra("placeId", restaurantList.get(position).getPlaceId());
        startActivity(intent);
    }
}
