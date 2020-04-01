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

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.UserHelper;
import com.example.go4lunch.view.fragments.ListRestaurantsFragment;
import com.example.go4lunch.view.fragments.ListWorkmatesFragment;
import com.example.go4lunch.view.fragments.MapViewFragment;
import com.facebook.places.model.PlaceFields;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

import butterknife.ButterKnife;

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

    private User currentUser;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;

    private static final int REQUEST_CODE = 101;


    private int AUTOCOMPLETE_REQUEST_CODE = 15;

    private void test ()
    {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        RectangularBounds rectangularBounds = RectangularBounds.newInstance
                (new LatLng(currentLocation.getLatitude()-25, currentLocation.getLongitude()-25),
                        new LatLng(currentLocation.getLatitude()+25, currentLocation.getLongitude()+25));

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setLocationBias(rectangularBounds)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(getApplicationContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

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
                currentLocation = location;
                //test();
            }
        });
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
            test();
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
                String name = place.getName();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        fetchLocation();

        this.displayFragment(displayMapViewFragment());
        this.configureBottomView();
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
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
        updateNavigationHeader();
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
                    return true;
                case R.id.action_listview :
                    displayFragment(displayListRestaurantsFragment());
                    return true;
                case R.id.action_workmates :
                    displayFragment(displayListWorkmatesFragment());
                    return true;
                default:
                    return false;
            }

        });
    }

    /**
     * Update the NavigationView's info {@link NavigationView}
     */
    private void updateNavigationHeader()
    {
        final View headerView = navigationView.getHeaderView(0);

        nameUser = headerView.findViewById(R.id.nav_header_name_txt);
        emailUser = headerView.findViewById(R.id.nav_header_email_txt);
        illustrationUser = headerView.findViewById(R.id.nav_header_image_view);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String userName = TextUtils.isEmpty(firebaseUser.getDisplayName()) ? getString(R.string.navigation_header_name) : firebaseUser.getDisplayName();
        nameUser.setText(userName);
        String userEmail = TextUtils.isEmpty(firebaseUser.getEmail()) ? getString(R.string.navigation_header_name) : firebaseUser.getEmail();
        emailUser.setText(userEmail);

        if (firebaseUser.getPhotoUrl() != null)
        {
            Glide.with(this).load(firebaseUser.getPhotoUrl()).circleCrop().into(illustrationUser);
        }
    }

    /**
     * Display a fragment
     * @param fragment
     */
    private void displayFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.navigation_drawer_frame_layout, fragment).commit();
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

    ///////////////////////////////////OVERRIDE METHODS///////////////////////////////////

    @Override
    public void onBackPressed() {

        if(this.drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.menu_drawer_lunch :
                this.showLunch();
            case R.id.menu_drawer_settings :
                break;
            case R.id.menu_drawer_logout :
                this.createAndShowPopUpLogOut();
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /////////////////////////////////// METHODS FOR MENU'S NAVIGATION VIEW ONCLICK ///////////////////////////////////

    /**
     * Find current User {@link UserHelper}
     * Check Boolean isChooseRestaurant {@link User}
     * Display a Toast or launch Details Activity
     */
    private void showLunch()
    {
        String uid  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserHelper.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                currentUser = documentSnapshot.toObject(User.class);

                if (currentUser.isChooseRestaurant())
                {
                    Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                    intent.putExtra("placeId", currentUser.getRestaurantChoose().getPlaceId());
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Aucun restaurant choisi", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    /**
     * Create and show an AlertDialog to logOut() {@link AlertDialog}
     */
    private void createAndShowPopUpLogOut()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("TITRE");
        builder.setMessage("Êtes-vous sûr de vouloir vous déconnecter ?");
        builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        logOut();
                    }
                });
        builder.setNegativeButton("Non", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Log Out from Firebase {@link AuthUI}
     */
    private void logOut()
    {
        AuthUI.getInstance().signOut(this).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null)
                {
                    Toast.makeText(getApplicationContext(), "Déconnexion réussie", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

}
