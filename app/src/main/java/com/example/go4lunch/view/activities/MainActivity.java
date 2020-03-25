package com.example.go4lunch.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
