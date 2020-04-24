package com.example.go4lunch.view.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.utils.Utils;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.example.go4lunch.view.adapters.ListRestaurantsAdapter;
import com.example.go4lunch.view_model.ViewModelGo4Lunch;
import com.example.go4lunch.view_model.factory.ViewModelFactoryGo4Lunch;
import com.example.go4lunch.view_model.injection.Injection;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ListRestaurantsFragment extends Fragment implements OnClickListenerItemList {

    private List<Restaurant> restaurantListFromPlaces;
    private ListRestaurantsAdapter adapter;
    private Location currentLocation;
    private ViewModelGo4Lunch viewModelGo4Lunch;
    private Disposable disposable;

    @BindView(R.id.fragment_list_restaurants_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress_bar_layout)
    ConstraintLayout progressBarLayout;
    @BindView(R.id.fragment_list_restaurants_menu_fab)
    FloatingActionMenu floatingActionButton;

    public ListRestaurantsFragment() {}

    private ListRestaurantsFragment(Location location)
    {
        this.currentLocation = location;
    }

    public static ListRestaurantsFragment newInstance(Location location)
    {
        return new ListRestaurantsFragment(location);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restaurantListFromPlaces = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_restaurants, container, false);
        ButterKnife.bind(this, v);
        this.progressBarLayout.setVisibility(View.VISIBLE);
        this.floatingActionButton.setVisibility(View.INVISIBLE);
        this.configRecyclerView();
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
        String key = getResources().getString(R.string.google_maps_key);   //BuildConfig.google_maps_key;
        this.viewModelGo4Lunch.getRestaurantsListPlacesMutableLiveData(currentLocation.getLatitude(), currentLocation.getLongitude(), 500, key)
                .observe(this, listObservable -> disposable = listObservable
                        .subscribeWith(new DisposableObserver<List<Restaurant>>() {
                            @Override
                            public void onNext(List<Restaurant> restaurantList) {
                                restaurantListFromPlaces = restaurantList;
                                Utils.updateDistanceToCurrentLocation(currentLocation, restaurantListFromPlaces);
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
        this.viewModelGo4Lunch.getRestaurantsListFirebaseMutableLiveData().observe(this, restaurantList -> {

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
            adapter.updateList(restaurantListFromPlaces);
            this.progressBarLayout.setVisibility(View.INVISIBLE);
            this.floatingActionButton.setVisibility(View.VISIBLE);
        });
    }

    ////////////////////////////////////////// ON CLICK  ///////////////////////////////////////////

    @OnClick(R.id.fragment_list_restaurants_near_me_fab)
    void triProximity() {
        Utils.sortProximity(restaurantListFromPlaces);
        this.adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.fragment_list_restaurants_rating_fab)
    void triRate() {
        Utils.sortRatingReverse(restaurantListFromPlaces);
        this.adapter.notifyDataSetChanged();
    }


    @OnClick(R.id.fragment_list_restaurants_name_fab)
    void triName() {
        Utils.sortName(restaurantListFromPlaces);
        this.adapter.notifyDataSetChanged();
    }

    ////////////////////////////////////////// CONFIGURE ///////////////////////////////////////////
    /**
     * Configure the RecyclerView
     */
    private void configRecyclerView() {
        this.adapter = new ListRestaurantsAdapter(Glide.with(this), this, getActivity());
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Unsubscribe of the HTTP Request
     */
    private void unsubscribe() {
        if (this.disposable != null && !this.disposable.isDisposed()) {
            this.disposable.dispose();
        }
    }

    ////////////////////////////////////////// OVERRIDE METHODS ///////////////////////////////////////////

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unsubscribe();
    }

    @Override
    public void onClickListener(int position) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra("placeId", restaurantListFromPlaces.get(position).getPlaceId());
        startActivity(intent);
    }

}
