package com.example.go4lunch.view.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.api.RestaurantHelper;
import com.example.go4lunch.model.api.RestaurantStreams;
import com.example.go4lunch.utils.StaticFields;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private Location currentLocation;
    //private FusedLocationProviderClient fusedLocationProviderClient;
    //private static final int REQUEST_CODE = 101;
    private SupportMapFragment supportMapFragment;
    private List<Restaurant> restaurantList;
    private Disposable disposable;
    private List<Restaurant> restaurantsWithWorkmates;
    //private String radiusStringEnter;



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

        currentLocation = StaticFields.CURRENT_LOCATION;
        restaurantList = StaticFields.RESTAURANTS_LIST;
        restaurantsWithWorkmates = StaticFields.RESTAURANTS_LIST_WITH_WORKMATES;

        //restaurantList = new ArrayList<>();
        //restaurantsWithWorkmates = new ArrayList<>();
        //this.stream(currentLocation.getLatitude(), currentLocation.getLongitude(), 500);
        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_view, container, false);
        this.supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(MapViewFragment.this);
        //fetchLocation();
        return v;
    }



    /*private void fetchLocation() {
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
    }*/

    /**
     * Update the workmate's number with documentSnapshot from Firebase
     */
    /*private void updateNumberWorkmates ()
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
    }*/



    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(Objects.requireNonNull(getContext()), R.raw.google_style);
        googleMap.setMapStyle(mapStyleOptions);

        for (int i = 0; i < restaurantList.size(); i ++)
        {
            Restaurant restaurantTemp = restaurantList.get(i);
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

            Marker markerFinal = googleMap.addMarker(tempMarker);
            markerFinal.setTag(restaurantTemp.getPlaceId());
            googleMap.setOnInfoWindowClickListener(this::lunchDetailsActivity);
        }

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(getResources().getString(R.string.map_view_fragment_my_position));
        float zoom = 16;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        googleMap.addMarker(markerOptions);



       /*googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                Projection projection = googleMap.getProjection();
                VisibleRegion visibleRegion = projection.getVisibleRegion();
                LatLng farRight = visibleRegion.farRight;
                LatLng farLeft = visibleRegion.farLeft;


                int radiusUpdate = calculateRectangularBoundsSinceCurrentLocation(farRight, farLeft);
                restaurants = stream(currentLocation.getLatitude(), currentLocation.getLongitude(), radiusUpdate);
            }
        });*/



    }

    //////////// A VOIR SI UTILE
    private int calculateRectangularBoundsSinceCurrentLocation(LatLng latLngRight, LatLng latLngLeft)
    {
        // L'objectif est de calculer la distance entre la position actuelle et les coins haut-gauche et haut-droit de l'ecran

        // ------------------------------------------------------------------------------------------------------------------
        // on se sert des formules suivantes :
        // convertion degree vers radian : 360 degrees = 2 PI radian => 1 degree = 2 PI / 360.
        final float DEG_EN_RADIAN = 2.0f * (float)Math.PI / 360.0f;

        // 1 degree en latitude = 111,32 km (111320 m)
        final float OUVERTURE_LAT_EN_METRES = 111320.0f;

        // 1 degree en longitude = 111,32 km * cos(latitude)    /!\ dans un fonction sinus, cosinu ou tangeante, la latitude doit etre exprimée en radian (et non pas en degree)
        // ici on utilise la latitude de la position courante car les points sont très - TRES - proches les uns des autres (c'est une approximation acceptable)
        final float OUVERTURE_LONG_EN_METRES = 111320.0f * (float)Math.cos(currentLocation.getLatitude() * DEG_EN_RADIAN);

        // theoreme de pythagore pour un triangle rectangle : c (hypothenuse) = √(a² + b²)

        // ------------------------------------------------------------------------------------------------------------------
        // Distance position actuelle <=> Coin haut-gauche
        float a_gauche, b_gauche;
        // ouverture de la latitude en degree ET convertion de l'ouverture en metres
        a_gauche = Math.abs((float)(latLngLeft.latitude - currentLocation.getLatitude())) * OUVERTURE_LAT_EN_METRES;

        // meme chose en longitude
        b_gauche = Math.abs((float)(latLngLeft.longitude - currentLocation.getLongitude())) * OUVERTURE_LONG_EN_METRES;

        // Math.sqrt() = fonction racine carree
        float dist_PositionCourante_CoinHautGauche = (float)Math.sqrt((a_gauche * a_gauche) + (b_gauche * b_gauche));

        // ------------------------------------------------------------------------------------------------------------------
        // Distance position actuelle <=> Coin haut-droit
        float a_droit, b_droit;
        a_droit = Math.abs((float)(latLngRight.latitude - currentLocation.getLatitude())) * OUVERTURE_LAT_EN_METRES;
        b_droit = Math.abs((float)(latLngRight.longitude - currentLocation.getLongitude())) * OUVERTURE_LONG_EN_METRES;
        float dist_PositionCourante_CoinHautDroit = (float)Math.sqrt((a_droit * a_droit) + (b_droit * b_droit));

        if (dist_PositionCourante_CoinHautDroit > 10000 || dist_PositionCourante_CoinHautGauche > 10000)
        {
            return 10000;
        }
        else if (dist_PositionCourante_CoinHautDroit >= dist_PositionCourante_CoinHautGauche)
        {
            return (int) dist_PositionCourante_CoinHautDroit;
        }
        else
        {
            return (int) dist_PositionCourante_CoinHautGauche;
        }

    }


   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                fetchLocation();
            }
        }
    }*/

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
    /*private List<Restaurant> stream(double lat, double lng, int radius)
    {
        String key = BuildConfig.google_maps_key;
        this.restaurantList.clear();

        this.disposable = RestaurantStreams.streamFetchRestaurantInList(lat, lng, radius, key).subscribeWith(new DisposableObserver<List<Restaurant>>() {
            @Override
            public void onNext(List<Restaurant> restaurantList) {

                MapViewFragment.this.restaurantList = restaurantList;
                updateNumberWorkmates();
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onComplete() {}
        });

        return restaurantList;
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


}
