package com.example.go4lunch.view.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.api.RestaurantHelper;
import com.example.go4lunch.model.api.RestaurantStreams;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private SupportMapFragment supportMapFragment;
    private List<Restaurant> restaurants;
    private Disposable disposable;
    private List<String> restaurantsWithWorkmates;
    private String radiusStringEnter;



    public MapViewFragment() {
        // Required empty public constructor
    }

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String key = BuildConfig.google_maps_key;
        Places.initialize(Objects.requireNonNull(getContext()), key);
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
                Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
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
        restaurantsWithWorkmates.clear();

        RestaurantHelper.getListRestaurants().addSnapshotListener(Objects.requireNonNull(getActivity()), (queryDocumentSnapshots, e) ->
        {
            if (queryDocumentSnapshots != null)
            {
                List<Restaurant> test = queryDocumentSnapshots.toObjects(Restaurant.class);
                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++)
                {
                    if (test.get(i).getUserList() != null && test.get(i).getUserList().size() > 0)
                    {
                        String placeId = (String) queryDocumentSnapshots.getDocuments().get(i).get("placeId");
                        restaurantsWithWorkmates.add(placeId);
                    }
                }
            }
            supportMapFragment.getMapAsync(MapViewFragment.this);
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(Objects.requireNonNull(getContext()), R.raw.google_style);
        googleMap.setMapStyle(mapStyleOptions);

        for (int i = 0; i < restaurants.size(); i ++)
        {
            Restaurant restaurantTemp = restaurants.get(i);
            LatLng tempLatLng = new LatLng(restaurantTemp.getLocation().getLat(), restaurantTemp.getLocation().getLng());
            MarkerOptions tempMarker = new MarkerOptions().position(tempLatLng).title(restaurantTemp.getName());

            if (restaurantsWithWorkmates.contains(restaurantTemp.getPlaceId()))
            {
                tempMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_green));

            }
            else
            {
                tempMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_orange));
            }

            Marker markerFinal = googleMap.addMarker(tempMarker);
            markerFinal.setTag(restaurantTemp.getPlaceId());
            googleMap.setOnInfoWindowClickListener(this::lunchDetailsActivity);
        }

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
        float zoom = 16;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        googleMap.addMarker(markerOptions);
        /*googleMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                updateStream();
            }
        });*/


       /* googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                Projection projection = googleMap.getProjection();
                VisibleRegion visibleRegion = projection.getVisibleRegion();
                LatLng latLngRight = visibleRegion.farRight;
                LatLng latLngLeft = visibleRegion.farLeft;

                double test = calculateRectangularBoundsSinceCurrentLocation(latLngRight, latLngLeft);
            }
        });*/



    }

    /*private double calculateRectangularBoundsSinceCurrentLocation(LatLng latLngRight, LatLng latLngLeft)
    {

        //double radiusTest = (latLngRight.longitude - latLngLeft.longitude)/2;

        double radiusTest;

        if (latLngRight.longitude - currentLocation.getLongitude() > currentLocation.getLongitude() - latLngLeft.longitude ||
                latLngRight.longitude - currentLocation.getLongitude() == currentLocation.getLongitude() - latLngLeft.longitude)
        {
           double x = 111 * Math.cos(latLngRight.latitude * (Math.PI/180.0f));
            radiusTest = x*(latLngRight.longitude - currentLocation.getLongitude());
        }
        else
        {
            radiusTest = latLngLeft.longitude*(111 * Math.cos(latLngLeft.latitude * (Math.PI/180.0f))) + currentLocation.getLongitude();
        }

        return radiusTest;

    }*/

   /* private int updateRadius(String enter)
    {
        int r = Integer.parseInt(enter);

        if (r > 10000)
        {
            r = 10000;
        }

        return r;
    }


    void updateStream()
    {
        this.createAndShowPopUpLogOut();
        int r = updateRadius(radiusStringEnter);
        restaurants = stream(currentLocation.getLatitude(), currentLocation.getLongitude(), r);
    }*/


    /**
     * Create and show an AlertDialog to logOut() {@link AlertDialog}
     */
    /*private void createAndShowPopUpLogOut()
    {


        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle("DISTANCE");
        builder.setMessage("A quelle distance voulez-vous voir des restaurants ? (limite : 10KM)");
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View v = layoutInflater.inflate(R.layout.alert_dialog_map_view_distance, null);
        builder.setView(v);
        TextInputEditText textInputEditText = v.findViewById(R.id.testEditTxt);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                radiusStringEnter = Objects.requireNonNull(textInputEditText.getText()).toString();
            }
        });
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }*/


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
            public void onError(Throwable e) {}

            @Override
            public void onComplete() {}
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
