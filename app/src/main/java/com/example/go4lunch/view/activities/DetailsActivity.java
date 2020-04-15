package com.example.go4lunch.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.go4lunch.R;
import com.example.go4lunch.view.fragments.DetailsFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    private String placeId;

    DetailsFragment detailsFragment;

   @BindView(R.id.details_activity_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        configureToolbar();
        Intent intent = getIntent();
        placeId = intent.getStringExtra("placeId");
        displayFragment(displayDetailsFragment());

    }

    /**
     * Configure the Toolbar {@link Toolbar}
     */
    private void configureToolbar()
    {
        setSupportActionBar(toolbar);
        //getSupportActionBar().setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_return);
    }

    /**
     * Display a fragment
     * @param fragment
     */
    private void displayFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.details_activity_frame_layout, fragment).commit();
    }

    /**
     * Display the MapViewFragment {@link DetailsFragment}
     */
    private DetailsFragment displayDetailsFragment()
    {
        if (this.detailsFragment == null)
        {
            this.detailsFragment = DetailsFragment.newInstance(placeId);
        }
        return this.detailsFragment;
    }

}
