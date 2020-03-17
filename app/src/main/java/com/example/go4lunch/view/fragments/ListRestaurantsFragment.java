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
import com.example.go4lunch.model.DetailPOJO;
import com.example.go4lunch.model.GenerateTests;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.RestaurantPOJO;
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
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class ListRestaurantsFragment extends Fragment{

    private List<Restaurant> restaurants = new ArrayList<>();
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
        configListRestaurants();
        //configRecyclerView();
        return v;

    }

    private void configRecyclerView()
    {
        this.adapter = new ListRestaurantsAdapter(restaurants, Glide.with(this));
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private static final int REQUEST_CODE = 12;
    private Location currentLocation;

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

    private List<Restaurant> stream(double lat, double lng, int radius)
    {
        String key = getResources().getString(R.string.google_maps_key);

        this.disposable = RestaurantStreams.streamFetchRestaurant(lat, lng, radius, key).subscribeWith(new DisposableObserver<RestaurantPOJO>() {
            @Override
            public void onNext(RestaurantPOJO restaurantPOJOS) {

                List<RestaurantPOJO.Result> res = restaurantPOJOS.getResults();

                for (int i = 0; i < res.size(); i ++)
                {

                    String name = res.get(i).getName();
                    String type = res.get(i).getTypes().get(0);
                    String address = res.get(i).getVicinity();
                    String photo = getPhoto(res.get(i).getPhotos().get(0).getPhotoReference(), 400, key);
                    String placeId = res.get(i).getPlaceId();

                    Restaurant restaurant = new Restaurant(name, type, address, photo, placeId);
                    restaurants.add(restaurant);
                }
                configRecyclerView();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }

        });







        /*this.disposable = RestaurantStreams.test1(lat, lng, radius, key).subscribeWith(new Observer<Restaurant>() {
            @Override
            public void onSubscribe(Disposable d) { }

            @Override
            public void onNext(Restaurant restaurant) { }

            @Override
            public void onError(Throwable e) { }

            @Override
            public void onComplete() { }
        });*/

       /* for (int i = 0; i < restaurants.size(); i ++)
        {
            Restaurant restaurant = restaurants.get(i);
            this.disposable = RestaurantStreams.streamDetailRestaurant(restaurants.get(i).getPlaceId(), key).subscribeWith(new DisposableObserver<DetailPOJO>() {
                @Override
                public void onNext(DetailPOJO detailPOJO)
                {
                    DetailPOJO.OpeningHours openingHours = detailPOJO.getResult().getOpeningHours();
                    restaurant.setOpeningHours(openingHours);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
        }*/

        return restaurants;
    }


    private List<Restaurant> streamDetail (String placeId)
    {
        String key = getResources().getString(R.string.google_maps_key);
        List<Restaurant> toPush = new ArrayList<>();


        for (int i = 0; i < restaurants.size(); i ++)
        {
            this.disposable = RestaurantStreams.streamDetailRestaurant(placeId, key).subscribeWith(new DisposableObserver<DetailPOJO>() {
                @Override
                public void onNext(DetailPOJO detailPOJO)
                {
                    String website = detailPOJO.getResult().getWebsite();
                    String phone_number = detailPOJO.getResult().getInternationalPhoneNumber();
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
        }



        return restaurants;
    }

    public String getPhoto(String photoReference, int maxWidth, String key)
    {
        return "https://maps.googleapis.com/maps/api/place/photo?" + "photoreference=" + photoReference
                + "&maxwidth=" + maxWidth + "&key=" + key;
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
