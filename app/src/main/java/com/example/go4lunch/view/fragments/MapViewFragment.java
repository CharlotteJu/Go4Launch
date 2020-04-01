package com.example.go4lunch.view.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.RestaurantHelper;
import com.example.go4lunch.model.api.RestaurantStreams;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.example.go4lunch.view.adapters.InfoWindowMapAdapter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static android.app.Activity.RESULT_OK;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private SupportMapFragment supportMapFragment;
    private List<Restaurant> restaurants;
    private Disposable disposable;
    private List<Restaurant> restaurantsWithWorkmates;


    private GoogleMap googleMap;


    public MapViewFragment() {
        // Required empty public constructor
    }

    public static MapViewFragment newInstance() {
        MapViewFragment fragment = new MapViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String key = BuildConfig.google_maps_key;
        Places.initialize(getContext(), key);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_view, container, false);
        this.supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        restaurants = new ArrayList<>();
        restaurantsWithWorkmates = new ArrayList<>();
        fetchLocation();
        return v;
    }



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
                currentLocation = location;
                restaurants = stream(currentLocation.getLatitude(), currentLocation.getLongitude(), 500);
            }
        });
    }

    private void updateNumberWorkmates ()
    {
        RestaurantHelper.getListRestaurants().addSnapshotListener(getActivity(), (queryDocumentSnapshots, e) ->
        {
            if (queryDocumentSnapshots != null)
            {
                List<Restaurant> test = queryDocumentSnapshots.toObjects(Restaurant.class);
                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++)
                {
                    if (test.get(i).getUserList() != null && test.get(i).getUserList().size() > 0)
                    {
                        String uidRestaurant = queryDocumentSnapshots.getDocuments().get(i).getId();
                        RestaurantHelper.getRestaurant(uidRestaurant).addOnSuccessListener(documentSnapshot -> {
                            Restaurant restaurantTemp = documentSnapshot.toObject(Restaurant.class);
                            restaurantsWithWorkmates.add(restaurantTemp);
                            supportMapFragment.getMapAsync(MapViewFragment.this);
                        });
                        break;
                    }
                }
                //supportMapFragment.getMapAsync(MapViewFragment.this);
            }

        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.google_style);
        this.googleMap.setMapStyle(mapStyleOptions);

        for (int i = 0; i < restaurants.size(); i ++)
        {
            Restaurant restaurantTemp = restaurants.get(i);
            LatLng tempLatLng = new LatLng(restaurantTemp.getLocation().getLat(), restaurantTemp.getLocation().getLng());
            MarkerOptions tempMarker = new MarkerOptions().position(tempLatLng).title(restaurantTemp.getName());

            if (restaurantsWithWorkmates.contains(restaurantTemp))
            {
                tempMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green));

            }
            else
            {
                tempMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_orange));
            }

            Marker markerFinal = this.googleMap.addMarker(tempMarker);
            markerFinal.setTag(restaurantTemp.getPlaceId());
            this.googleMap.setOnInfoWindowLongClickListener(this::lunchDetailsActivity);

            /*this.googleMap.setOnMarkerClickListener(marker -> {
                marker.showInfoWindow();
                return true;
            });*/
        }


        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
        float zoom = 16;
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        this.googleMap.addMarker(markerOptions);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                fetchLocation();
            }
        }
    }

    private void lunchDetailsActivity(Marker marker)
    {
        String placeId = (String) marker.getTag();
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra("placeId", placeId);
        startActivity(intent);
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
        String key = BuildConfig.google_maps_key;
        this.restaurants.clear();

        this.disposable = RestaurantStreams.streamFetchRestaurantInList(lat, lng, radius, key).subscribeWith(new DisposableObserver<List<Restaurant>>() {
            @Override
            public void onNext(List<Restaurant> restaurantList) {

                restaurants = restaurantList;
                updateNumberWorkmates();

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
    public void onDestroy() {
        super.onDestroy();
        this.unsubscribe();
    }


}
