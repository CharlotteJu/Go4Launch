package com.example.go4lunch.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.go4lunch.R;
import com.example.go4lunch.view.fragments.ListRestaurantsFragment;
import com.example.go4lunch.view.fragments.ListWorkmatesFragment;
import com.example.go4lunch.view.fragments.MapViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    //FOR DESIGN
    @BindView(R.id.activity_main_bottom_navigation)
    BottomNavigationView bottomNavigationView;


    //FOR DATA
    MapViewFragment mapViewFragment;
    ListRestaurantsFragment listRestaurantsFragment;
    ListWorkmatesFragment listWorkmatesFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        displayFragment(displayMapViewFragment());
        configureBottomView();
    }


    //CONFIGURE VIEW
    private void displayFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_frame_layout, fragment).commit();
    }

    private MapViewFragment displayMapViewFragment()
    {
        if (this.mapViewFragment == null)
        {
            this.mapViewFragment = MapViewFragment.newInstance();
        }
        return this.mapViewFragment;
    }

    private ListRestaurantsFragment displayListRestaurantsFragment()
    {
        if (this.listRestaurantsFragment == null)
        {
            this.listRestaurantsFragment = ListRestaurantsFragment.newInstance();
        }
        return this.listRestaurantsFragment;
    }

    private ListWorkmatesFragment displayListWorkmatesFragment()
    {
        if (this.listWorkmatesFragment == null)
        {
            this.listWorkmatesFragment = ListWorkmatesFragment.newInstance();
        }
        return listWorkmatesFragment;
    }


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


}
