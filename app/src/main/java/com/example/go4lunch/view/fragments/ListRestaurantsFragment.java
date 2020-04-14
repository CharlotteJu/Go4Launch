package com.example.go4lunch.view.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.utils.StaticFields;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.example.go4lunch.view.adapters.ListRestaurantsAdapter;
import com.example.go4lunch.view_model.ViewModelGo4Lunch;
import com.example.go4lunch.view_model.factory.ViewModelFactoryGo4Lunch;
import com.example.go4lunch.view_model.injection.Injection;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class ListRestaurantsFragment extends Fragment implements OnClickListener {

    private List<Restaurant> restaurantListFromPlaces = new ArrayList<>();
    private ListRestaurantsAdapter adapter;
    private static final int REQUEST_CODE = 12;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private ViewModelGo4Lunch viewModelGo4Lunch;
    private Disposable disposable;
    private List<Restaurant> restaurantListWithWorkmates = new ArrayList<>();


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

        //this.currentLocation = StaticFields.CURRENT_LOCATION;
        //this.restaurantListFromPlaces = StaticFields.RESTAURANTS_LIST;
        //this.updateDistanceToCurrentLocation();

        this.configViewModel();
        this.fetchLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_restaurants, container, false);
        ButterKnife.bind(this, v);

        this.configRecyclerView();
        return v;

    }


    ////////////////////////////////////////// VIEW MODEL ///////////////////////////////////////////

    private void configViewModel()
    {
        ViewModelFactoryGo4Lunch viewModelFactoryGo4Lunch = Injection.viewModelFactoryGo4Lunch();
        viewModelGo4Lunch = ViewModelProviders.of(this, viewModelFactoryGo4Lunch).get(ViewModelGo4Lunch.class);
    }

    private void getRestaurantListFromPlaces()
    {
        String key = BuildConfig.google_maps_key;
        this.viewModelGo4Lunch.getRestaurantsListPlacesMutableLiveData(currentLocation.getLatitude(), currentLocation.getLongitude(), 500, key)
                .observe(this, listObservable -> disposable = listObservable
                        .subscribeWith(new DisposableObserver<List<Restaurant>>() {
                            @Override
                            public void onNext(List<Restaurant> restaurantList) {
                                restaurantListFromPlaces = restaurantList;
                                updateDistanceToCurrentLocation();
                                getRestaurantListFromFirebase();
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onComplete() {
                            }
                        }));
    }

    private void getRestaurantListFromFirebase()
    {
        this.viewModelGo4Lunch.getRestaurantsListFirebaseMutableLiveData().observe(this, restaurantList -> {
            for (int i = 0; i < restaurantList.size(); i++)
            {
                Restaurant restaurant = restaurantList.get(i);

                if (restaurant.getUserList().size() > 0)
                {
                    if (restaurantListFromPlaces.contains(restaurant))
                    {
                        restaurantListFromPlaces.get(i).setUserList(restaurant.getUserList());
                    }
                }
            }
            adapter.updateList(restaurantListFromPlaces);
        });
    }


    ////////////////////////////////////////// CONFIGURE ///////////////////////////////////////////


    /**
     * Configure the RecyclerView
     */
    private void configRecyclerView() {
        //this.adapter = new ListRestaurantsAdapter(restaurantList, Glide.with(this), this, getActivity(), currentLocation);
        this.adapter = new ListRestaurantsAdapter(Glide.with(this), this, getActivity());
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    /**
     * Fetch the current location {@link ActivityCompat} {@link Location}
     * Set a value to the static field CURRENT_LOCATION
     * We can display 1st fragment when we have the location
     */
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                StaticFields.CURRENT_LOCATION = location;
                currentLocation = location;
                getRestaurantListFromPlaces();
            }
        });
    }


    @OnClick(R.id.fragment_list_restaurants_near_me_fab)
    void triProximity() {
        Collections.sort(restaurantListFromPlaces, (o1, o2) -> {
            Integer restau1 = o1.getDistanceCurrentUser();
            Integer restau2 = o2.getDistanceCurrentUser();

            return restau1.compareTo(restau2);

        });
        this.adapter.notifyDataSetChanged();

    }

    @OnClick(R.id.fragment_list_restaurants_rating_fab)
    void triRate() {
        Collections.sort(restaurantListFromPlaces, (o1, o2) -> {
            Double restau1 = o1.getRating();
            Double restau2 = o2.getRating();

            return restau1.compareTo(restau2);
        });

        Collections.reverse(restaurantListFromPlaces);

        this.adapter.notifyDataSetChanged();
    }


    @OnClick(R.id.fragment_list_restaurants_name_fab)
    void triName() {
        Collections.sort(restaurantListFromPlaces, (o1, o2) -> {

            String restaurant1 = o1.getName();
            String restaurant2 = o2.getName();

            return restaurant1.compareTo(restaurant2);
        });

        this.adapter.notifyDataSetChanged();
    }

    /**
     * Update the attribute DistanceCurrentUser for each restaurant
     */
    private void updateDistanceToCurrentLocation() {
        Location restaurantLocation = new Location("fusedLocationProvider");

        for (int i = 0; i < restaurantListFromPlaces.size(); i++) {
            //Get the restaurant's location
            restaurantLocation.setLatitude(restaurantListFromPlaces.get(i).getLocation().getLat());
            restaurantLocation.setLongitude(restaurantListFromPlaces.get(i).getLocation().getLng());
            //Get the distance between currentLocation and restaurantLocation
            int distanceLocation = (int) currentLocation.distanceTo(restaurantLocation);

            restaurantListFromPlaces.get(i).setDistanceCurrentUser(distanceLocation);
        }
    }

    /**
     * Unsubscribe of the HTTP Request
     */
    private void unsubscribe() {
        if (this.disposable != null && !this.disposable.isDisposed()) {
            this.disposable.dispose();
        }
    }

    ////////////////////////////////////////// OVERRIDE METHODS ///////////////////////////////////////////

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unsubscribe();
    }

    @Override
    public void onClickListener(int position) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra("placeId", restaurantListFromPlaces.get(position).getPlaceId());
        startActivity(intent);
    }

    ///////////////////////////////////ANCIENNES METHODES///////////////////////////////////
    /**
     * Configure the List<Restaurant> while checking the Access Permission
     */
     /*private void configListRestaurants()
    {
        //currentLocation = StaticFields.CURRENT_LOCATION;
        //stream(currentLocation.getLatitude(), currentLocation.getLongitude(), 500);

        if ((ActivityCompat.checkSelfPermission(
                Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                //stream(currentLocation.getLatitude(), currentLocation.getLongitude(), 500);
            }
        });


    }*/

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
    }*/
}
