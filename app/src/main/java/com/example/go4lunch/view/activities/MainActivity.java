package com.example.go4lunch.view.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.view_model.ViewModelGo4Lunch;
import com.example.go4lunch.view_model.factory.ViewModelFactoryGo4Lunch;
import com.example.go4lunch.view_model.injection.Injection;
import com.example.go4lunch.view_model.repositories.RestaurantFirebaseRepository;
import com.example.go4lunch.view_model.repositories.RestaurantPlacesRepository;
import com.example.go4lunch.view_model.repositories.UserFirebaseRepository;
import com.example.go4lunch.utils.StaticFields;
import com.example.go4lunch.view.fragments.ListRestaurantsFragment;
import com.example.go4lunch.view.fragments.ListWorkmatesFragment;
import com.example.go4lunch.view.fragments.MapViewFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //FOR DESIGN
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation_drawer)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_drawer_nav_view)
    NavigationView navigationView;

    TextView nameUser;
    TextView emailUser;
    ImageView illustrationUser;

    //FOR DATA
    MapViewFragment mapViewFragment;
    ListRestaurantsFragment listRestaurantsFragment;
    ListWorkmatesFragment listWorkmatesFragment;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    private Disposable disposable;
    private ViewModelGo4Lunch viewModelGo4Lunch;
    private User currentUser;


    //private List<Restaurant> restaurantsWithWorkmates = new ArrayList<>();
    //private List<Restaurant> restaurantsFromPlaces = new ArrayList<>();

    private static final int REQUEST_CODE = 101;
    private int AUTOCOMPLETE_REQUEST_CODE = 15;
    private static final String NOTIFICATIONS_SHARED_PREFERENCES = "PREF_NOTIF";
    private static final String NOTIFICATIONS_BOOLEAN = "NOTIFICATIONS_BOOLEAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());

        this.fetchLocation();
        this.configViewModel();

        this.displayFragment(displayMapViewFragment());
        this.configureBottomView();
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
    }



    ///////////////////////////////////VIEW MODEL///////////////////////////////////

    private void configViewModel()
    {
        ViewModelFactoryGo4Lunch viewModelFactoryGo4Lunch = Injection.viewModelFactoryGo4Lunch();
        viewModelGo4Lunch= ViewModelProviders.of(this, viewModelFactoryGo4Lunch).get(ViewModelGo4Lunch.class);
        this.getCurrentUser();
    }

    private void getCurrentUser()
    {
        String uid = FirebaseAuth.getInstance().getUid();
        this.viewModelGo4Lunch.getUserCurrentMutableLiveData(uid).observe(this, user -> {
            updateNavigationHeader(user);
            currentUser = user;

        });
    }

    ///////////////////////////////////CONFIGURE METHODS///////////////////////////////////

    /**
     * Configure the Toolbar {@link Toolbar}
     */
    private void configureToolbar()
    {
        setSupportActionBar(toolbar);

    }

    /**
     * Configure the toolbar search with {@link Autocomplete}
     */
    private void configureAutocompleteSearchToolbar()
    {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        List<LatLng> latlngForRectangularBounds = calculateRectangularBoundsSinceCurrentLocation(0.5);
        RectangularBounds rectangularBounds = RectangularBounds.newInstance
                (latlngForRectangularBounds.get(0), latlngForRectangularBounds.get(1));

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setLocationRestriction(rectangularBounds)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(getApplicationContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    /**
     * Configure the DrawerLayout {@link DrawerLayout}
     */
    private void configureDrawerLayout()
    {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                                        R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Configure the NavigationView {@link NavigationView}
     */
    private void configureNavigationView()
    {
        navigationView.setNavigationItemSelectedListener(this);
        //updateNavigationHeader();
    }

    /**
     * Configure the BottomView {@link BottomNavigationView}
     */
    private void configureBottomView()
    {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId())
            {
                case R.id.action_mapview :
                    displayFragment(displayMapViewFragment());
                    this.toolbar.getMenu().findItem(R.id.toolbar_menu_search).setVisible(true);
                    return true;
                case R.id.action_listview :
                    displayFragment(displayListRestaurantsFragment());
                    this.toolbar.getMenu().findItem(R.id.toolbar_menu_search).setVisible(true);
                    return true;
                case R.id.action_workmates :
                    displayFragment(displayListWorkmatesFragment());
                    this.toolbar.getMenu().findItem(R.id.toolbar_menu_search).setVisible(false);
                    return true;
                default:
                    return false;
            }

        });
    }

    /**
     * Update the NavigationView's info {@link NavigationView}
     */
    private void updateNavigationHeader(User currentUser)
    {
        final View headerView = navigationView.getHeaderView(0);

        nameUser = headerView.findViewById(R.id.nav_header_name_txt);
        emailUser = headerView.findViewById(R.id.nav_header_email_txt);
        illustrationUser = headerView.findViewById(R.id.nav_header_image_view);

        //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //String userName = TextUtils.isEmpty(firebaseUser.getDisplayName()) ? getString(R.string.navigation_header_name) : firebaseUser.getDisplayName();
        //String userEmail = TextUtils.isEmpty(firebaseUser.getEmail()) ? getString(R.string.navigation_header_name) : firebaseUser.getEmail();

        nameUser.setText(currentUser.getName());
        emailUser.setText(currentUser.getEmail());

        if (currentUser.getIllustration() != null)
        {
            Glide.with(this).load(currentUser.getIllustration()).circleCrop().into(illustrationUser);
        }
    }

    /**
     * Display a fragment
     * @param fragment
     */
    private void displayFragment(Fragment fragment)
    {
        //TODO : Vérifier l'ajout à la backstack

        getSupportFragmentManager().beginTransaction().replace(R.id.navigation_drawer_frame_layout, fragment).addToBackStack("backstack").commit();

    }

    /**
     * Display the MapViewFragment {@link MapViewFragment}
     */
    private MapViewFragment displayMapViewFragment()
    {
        if (this.mapViewFragment == null)
        {
            this.mapViewFragment = MapViewFragment.newInstance();
        }
        return this.mapViewFragment;
    }

    /**
     * Display the ListRestaurantsFragment {@link ListRestaurantsFragment}
     */
    private ListRestaurantsFragment displayListRestaurantsFragment()
    {
        if (this.listRestaurantsFragment == null)
        {
            this.listRestaurantsFragment = ListRestaurantsFragment.newInstance();
        }
        return this.listRestaurantsFragment;
    }

    /**
     * Display the ListWorkmatesFragment {@link ListWorkmatesFragment}
     */
    private ListWorkmatesFragment displayListWorkmatesFragment()
    {
        if (this.listWorkmatesFragment == null)
        {
            this.listWorkmatesFragment = ListWorkmatesFragment.newInstance();
        }
        return listWorkmatesFragment;
    }



    //// A VOIR SI UTILE
    private List<LatLng> calculateRectangularBoundsSinceCurrentLocation(double radius)
    {
        List<LatLng> list = new ArrayList<>();

        double latA = currentLocation.getLatitude() - (radius/111);
        double lngA =  currentLocation.getLongitude() - (radius/(111 * Math.cos(latA * (Math.PI/180.0f)))) ;
        LatLng pointA = new LatLng(latA, lngA);
        list.add(pointA);


        double latB = currentLocation.getLatitude() + radius/111 ;
        double lngB = currentLocation.getLongitude() + radius/(111 * Math.cos(latB * (Math.PI/180.0f)));

        LatLng pointB = new LatLng(latB, lngB);
        list.add(pointB);

        return list;
    }

    ///////////////////////////////////GET CURRENT INFORMATION///////////////////////////////////



    /**
     * Fetch the current location {@link ActivityCompat} {@link Location}
     * Set a value to the static field CURRENT_LOCATION
     * We can display 1st fragment when we have the location
     */
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                StaticFields.CURRENT_LOCATION = location;
                currentLocation = location;

               // this.streamRestaurantsFromPlaces(currentLocation.getLatitude(), currentLocation.getLongitude(), 500);
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



    ///////////////////////////////////OVERRIDE METHODS///////////////////////////////////


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unsubscribe();
    }

    @Override
    public void onBackPressed() {

        if(this.drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.menu_drawer_lunch :
                this.showLunch();
                break;
            case R.id.menu_drawer_settings :
                this.createAndShowPopUpSettings();
                break;
            case R.id.menu_drawer_logout :
                this.createAndShowPopUpLogOut();
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.toolbar_menu_search)
        {
            configureAutocompleteSearchToolbar();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                String placeId = place.getId();
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("placeId", placeId);
                startActivity(intent);

            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR)
            {
                assert data != null;
                Status status = Autocomplete.getStatusFromIntent(data);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    /////////////////////////////////// METHODS FOR MENU'S NAVIGATION VIEW ONCLICK ///////////////////////////////////

    /**
     * Find current User {@link UserFirebaseRepository}
     * Check Boolean isChooseRestaurant {@link User}
     * Display a Toast or launch Details Activity
     */
    private void showLunch()
    {
        if (currentUser.isChooseRestaurant())
        {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra("placeId", currentUser.getRestaurantChoose().getPlaceId());
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.main_activity_no_choose_restaurant), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create and show an AlertDialog to logOut() {@link AlertDialog}
     */
    private void createAndShowPopUpLogOut()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.main_activity_pop_up_log_out_title));
        builder.setMessage(getResources().getString(R.string.main_activity_pop_up_log_out_message));
        builder.setPositiveButton(getResources().getString(R.string.main_activity_pop_up_yes), (dialogInterface, i) -> logOut());
        builder.setNegativeButton(getResources().getString(R.string.main_activity_pop_up_no), null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Log Out from Firebase {@link AuthUI}
     */
    private void logOut()
    {
        AuthUI.getInstance().signOut(this).addOnSuccessListener(this, aVoid ->
        {
            if (FirebaseAuth.getInstance().getCurrentUser() == null)
            {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.main_activity_success_sign_out), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Create and show an AlertDialog to updateSharedPreferences() {@link AlertDialog}
     */
    private void createAndShowPopUpSettings()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.main_activity_pop_up_notifications_title));
        builder.setMessage(getResources().getString(R.string.main_activity_pop_up_notifications_message));
        builder.setPositiveButton(getResources().getString(R.string.main_activity_pop_up_yes),
                (dialogInterface, i) -> updateSharedPreferences(true));
        builder.setNegativeButton(getResources().getString(R.string.main_activity_pop_up_no),
                (dialog, which) -> updateSharedPreferences(false));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * Update SharedPreferences for notifications {@link SharedPreferences}
     * @param notificationsAuthorized
     */
    private void updateSharedPreferences(boolean notificationsAuthorized)
    {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(NOTIFICATIONS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATIONS_BOOLEAN, notificationsAuthorized);
        editor.commit();
    }


    ///////////////////////////////////ANCIENNES METHODES///////////////////////////////////

    /**
     * Get the current User {@link UserFirebaseRepository} {@link User}
     * Set a value to the static fields CURRENT_USER and IUD USER
     * We can update the NavigationHeader when we have user
     */
   /* private void getCurrentUser()
    {
        String uidUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        viewModelGo4Lunch.setUserCurrentMutableLiveData(uidUser);

        /*UserFirebaseRepository.getUser(uidUser).addOnSuccessListener(documentSnapshot -> {
            StaticFields.CURRENT_USER = documentSnapshot.toObject(User.class);
            StaticFields.IUD_USER = uidUser;
            updateNavigationHeader();
        });
    }*/

     /*private void getRestaurantsListPlaces ()
    {
        String key = BuildConfig.google_maps_key;
        viewModelGo4Lunch.getsetRestaurantsListPlacesMutableLiveData(currentLocation.getLatitude(), currentLocation.getLongitude(), 500, key)
                .observe(this, listObservable ->
                        disposable = listObservable.subscribeWith(new DisposableObserver<List<Restaurant>>() {
            @Override
            public void onNext(List<Restaurant> restaurantList)
            {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }*/

    /**
     * Recover the List<Restaurant> with the HTTP Request
     * @param lat double with latitude of the current User
     * @param lng double with longitude of the current User
     * @param radius double to define the distance around the current User
     */
    /*private void streamRestaurantsFromPlaces(double lat, double lng, int radius)
    {
            String key = BuildConfig.google_maps_key;

            this.disposable = RestaurantPlacesRepository.streamFetchRestaurantInList(lat, lng, radius, key).subscribeWith(new DisposableObserver<List<Restaurant>>() {
                @Override
                public void onNext(List<Restaurant> restaurantList)
                {
                    StaticFields.RESTAURANTS_LIST = restaurantList;
                    getRestaurantListWithWorkmates();
                    displayFragment(displayMapViewFragment());
                }

                @Override
                public void onError(Throwable e) {}

                @Override
                public void onComplete() {}
            });

    }*/

    /*private void getRestaurantListWithWorkmates()
    {

        viewModelGo4Lunch.restaurantsListFirebaseMutableLiveData.observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurantList)
            {
                List<Restaurant> listTemp = restaurantList;

                for (int i = 0; i < listTemp.size(); i ++)
                {
                    if (listTemp.get(i).getUserList().size() > 0)
                    {
                        restaurantsWithWorkmates.add(listTemp.get(i));
                    }
                }
            }
        });

        /*RestaurantFirebaseRepository.getListRestaurants().addSnapshotListener((queryDocumentSnapshots, e) ->
        {
            if (queryDocumentSnapshots != null)
            {
                List<Restaurant> restaurantListFromFirebase = queryDocumentSnapshots.toObjects(Restaurant.class);

                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++)
                {
                    Restaurant restaurantTemp = restaurantListFromFirebase.get(i);

                    if (StaticFields.RESTAURANTS_LIST.contains(restaurantTemp))
                    {
                        if (restaurantTemp.getUserList() != null && restaurantTemp.getUserList().size() > 0)
                        {
                            restaurantsWithWorkmates.add(restaurantTemp);
                        }
                    }

                }

                StaticFields.RESTAURANTS_LIST_WITH_WORKMATES = restaurantsWithWorkmates;
            }

        });


    }*/



}
