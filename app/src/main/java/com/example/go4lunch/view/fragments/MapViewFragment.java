package com.example.go4lunch.view.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.utils.UtilsCalcul;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.example.go4lunch.view_model.ViewModelGo4Lunch;
import com.example.go4lunch.view_model.factory.ViewModelFactoryGo4Lunch;
import com.example.go4lunch.view_model.injection.Injection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    //FOR DESIGN
    @BindView(R.id.progress_bar_layout)
    ConstraintLayout progressBarLayout;

    //FOR DATA
    private Location currentLocation;
    private List<Restaurant> restaurantListFromPlaces;
    private Disposable disposable;
    private ViewModelGo4Lunch viewModelGo4Lunch;
    private GoogleMap googleMap;
    private int radius;
    private float zoom;
    private String key;

    public MapViewFragment() {}

    private MapViewFragment (Location location)
    {
        this.currentLocation = location;
    }

    public static MapViewFragment newInstance(Location location)
    {
        return new MapViewFragment(location);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.key = getResources().getString(R.string.google_maps_key);
        Places.initialize(Objects.requireNonNull(getContext()), key);
        this.radius = 500;
        this.zoom = 0;
        restaurantListFromPlaces = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_map_view, container, false);
        ButterKnife.bind(this, v);
        this.progressBarLayout.setVisibility(View.VISIBLE);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (supportMapFragment != null)
        {
            supportMapFragment.getMapAsync(MapViewFragment.this);
        }
        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.configViewModel();
    }

    public int getRadius()
    {
        return this.radius;
    }

    ////////////////////////////////////////// VIEW MODEL ///////////////////////////////////////////

    private void configViewModel()
    {
        ViewModelFactoryGo4Lunch viewModelFactoryGo4Lunch = Injection.viewModelFactoryGo4Lunch();
        viewModelGo4Lunch = ViewModelProviders.of(this, viewModelFactoryGo4Lunch).get(ViewModelGo4Lunch.class);
        this.getRestaurantListFromPlaces();
    }

    private void getRestaurantListFromPlaces()
    {
        this.viewModelGo4Lunch.getRestaurantsListPlacesMutableLiveData(currentLocation.getLatitude(), currentLocation.getLongitude(), radius, key)
                .observe(this, listObservable -> disposable = listObservable
                        .subscribeWith(new DisposableObserver<List<Restaurant>>()
                        {
                            @Override
                            public void onNext(List<Restaurant> restaurantList)
                            {
                                restaurantListFromPlaces = restaurantList;
                                getRestaurantListFromFirebase();

                            }
                            @Override
                            public void onError(Throwable e) {}
                            @Override
                            public void onComplete() {}
                        }));
    }

    private void getRestaurantListFromFirebase()
    {
        this.viewModelGo4Lunch.getRestaurantsListFirebaseMutableLiveData().observe(this, restaurantList ->
        {
            int size = restaurantList.size();
            for (int i = 0; i < size; i++)
            {
                Restaurant restaurant = restaurantList.get(i);

                if (restaurant.getUserList().size() > 0)
                {
                    if (restaurantListFromPlaces.contains(restaurant))
                    {
                        int index = restaurantListFromPlaces.indexOf(restaurant);
                        restaurantListFromPlaces.get(index).setUserList(restaurant.getUserList());
                    }
                }
            }
            this.progressBarLayout.setVisibility(View.INVISIBLE);
            setMarker();
        });
    }

    ////////////////////////////////////////// CONFIGURE ///////////////////////////////////////////

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(Objects.requireNonNull(getContext()), R.raw.google_style);
        this.googleMap.setMapStyle(mapStyleOptions);
    }

    /**
     * Lunch Details Activity when we click on a Restaurant Marker
     * @param marker from the Google Map
     */
    private void lunchDetailsActivity(Marker marker)
    {
        String placeId = (String) marker.getTag();
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra("placeId", placeId);
        startActivity(intent);
    }

    /**
     * Set the markers on GoogleMap
     */
    private void setMarker()
    {
        if (this.googleMap != null)
        {
            this.googleMap.clear();
        }

        int size = restaurantListFromPlaces.size();
        for (int i = 0; i < size; i ++)
        {
            Restaurant restaurantTemp = restaurantListFromPlaces.get(i);
            LatLng tempLatLng = new LatLng(restaurantTemp.getLocation().getLat(), restaurantTemp.getLocation().getLng());
            MarkerOptions tempMarker = new MarkerOptions().position(tempLatLng).title(restaurantTemp.getName());

            if (restaurantTemp.getUserList() != null)
            {
                if (restaurantTemp.getUserList().size() > 0)
                {
                    tempMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green));
                }
            }
            if (tempMarker.getIcon() == null)
            {
                tempMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_orange));
            }

            Marker markerFinal = googleMap.addMarker(tempMarker);
            markerFinal.setTag(restaurantTemp.getPlaceId());
            this.googleMap.setOnInfoWindowClickListener(this::lunchDetailsActivity);
        }

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(getResources().getString(R.string.map_view_fragment_my_position));
        if (this.zoom == 0)
        {
            this.zoom = 16;
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }
        this.googleMap.addMarker(markerOptions);
        this.googleMap.setOnCameraIdleListener(this::getBoundsZoom);
    }

    /**
     * Get Bounds according to the currentZoom
     */
    private void getBoundsZoom()
    {
        Projection projection = googleMap.getProjection();
        VisibleRegion visibleRegion = projection.getVisibleRegion();
        LatLng latLngRight = visibleRegion.farRight;
        LatLng latLngLeft = visibleRegion.farLeft;

        int oldRadius = radius;

        radius = (int) UtilsCalcul.calculateRadiusAccordingToCurrentLocation(latLngRight, latLngLeft, currentLocation);

        if (radius <= oldRadius - 100 || radius >= oldRadius + 100)
        {
            this.getRestaurantListFromPlaces();
        }
    }

    /////////////////////////////////// DESTROY METHODS ///////////////////////////////////

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
    public void onDestroy()
    {
        super.onDestroy();
        this.unsubscribe();
    }
}
