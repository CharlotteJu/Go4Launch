package com.example.go4lunch.view.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.api.RestaurantStreams;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private SupportMapFragment supportMapFragment;
    private List<Restaurant> restaurants;
    private Disposable disposable;


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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_view, container, false);
        this.supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        restaurants = new ArrayList<>();
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
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    restaurants = stream(currentLocation.getLatitude(), currentLocation.getLongitude(), 500);
                    //supportMapFragment.getMapAsync(MapViewFragment.this::onMapReady);
                }
            }
        });
    }

   /* private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId)
    {
        Drawable drawable = ContextCompat.getDrawable(context, vectorResId);
        //drawable.setBounds(0,0,drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }*/

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, 8, 8);
        Bitmap bitmap = Bitmap.createBitmap(8, 8, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        for (int i = 0; i < restaurants.size(); i ++)
        {
            LatLng tempLatLng = new LatLng(restaurants.get(i).getLocation().getLat(), restaurants.get(i).getLocation().getLng());
            MarkerOptions tempMarker = new MarkerOptions().position(tempLatLng).title(restaurants.get(i).getName());
            tempMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_orange));
            this.googleMap.addMarker(tempMarker);
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
        String key = getActivity().getResources().getString(R.string.google_maps_key);
        this.restaurants.clear();

        this.disposable = RestaurantStreams.streamFetchRestaurantInList(lat, lng, radius, key).subscribeWith(new DisposableObserver<List<Restaurant>>() {
            @Override
            public void onNext(List<Restaurant> restaurantList) {

                restaurants = restaurantList;
                supportMapFragment.getMapAsync(MapViewFragment.this::onMapReady);
                //TODO : Mettre les puces
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
