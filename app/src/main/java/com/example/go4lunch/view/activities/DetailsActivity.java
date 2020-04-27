package com.example.go4lunch.view.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.go4lunch.R;
import com.example.go4lunch.view.fragments.DetailsFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_return);
    }

    /**
     * Display a fragment
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

   @Override
    public boolean onSupportNavigateUp() {
        this.onBackPressed();
        return true;
    }
}
