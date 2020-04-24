package com.example.go4lunch.view.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
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
import com.google.android.gms.maps.model.CameraPosition;
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

    private Location currentLocation;
    private List<Restaurant> restaurantListFromPlaces;
    private Disposable disposable;
    private ViewModelGo4Lunch viewModelGo4Lunch;
    private GoogleMap googleMap;
    private int radius;

    @BindView(R.id.progress_bar_layout)
    ConstraintLayout progressBarLayout;

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
        String key = BuildConfig.google_maps_key;
        Places.initialize(Objects.requireNonNull(getContext()), key);
        this.radius = 500;
        restaurantListFromPlaces = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    public void onResume() {
        super.onResume();
        this.configViewModel();
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
        String key = BuildConfig.google_maps_key;
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
        float zoom = 16;
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        this.googleMap.addMarker(markerOptions);
        //this.googleMap.setOnCameraIdleListener(this::getBoundsZoom);
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

        radius = calculateRectangularBoundsSinceCurrentLocation(latLngRight, latLngLeft);
        //this.getRestaurantListFromPlaces();
    }

    //////////// TODO : A VOIR SI UTILE
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

    ///////////////////////////////////OVERRIDE METHODS///////////////////////////////////

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
