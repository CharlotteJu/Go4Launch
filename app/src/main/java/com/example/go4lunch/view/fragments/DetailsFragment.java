package com.example.go4lunch.view.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.RestaurantStreams;
import com.example.go4lunch.model.api.UserHelper;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private String placeId;
    private Restaurant restaurantFinal;
    private Disposable disposable;
    private User currentUser;
    private String uid;

    @BindView(R.id.details_fragment_name_restaurant_txt)
    TextView name;
    @BindView(R.id.details_fragment_illustration_image)
    ImageView illustration;
    @BindView(R.id.details_fragment_address_txt)
    TextView address;
    @BindView(R.id.details_fragment_star_1_image)
    ImageView star1;
    @BindView(R.id.details_fragment_star_2_image)
    ImageView star2;
    @BindView(R.id.details_fragment_star_3_image)
    ImageView star3;
    @BindView(R.id.details_fragment_choose_button)
    FloatingActionButton chooseFAB;
    @BindView(R.id.details_fragment_call_txt)
    TextView testCall;


    @OnClick(R.id.details_fragment_call_button)
    void onClickCallButton()
    {
        testCall.setText(restaurantFinal.getPhoneNumber());
    }

    @OnClick(R.id.details_fragment_like_button)
    void onClickLikeButton()
    {
        List<Restaurant> restaurantList;
        if (currentUser.getRestaurantListFavorites() == null)
        {
            restaurantList = new ArrayList<>();
        }
        else
        {
            restaurantList = currentUser.getRestaurantListFavorites();
        }

        restaurantList.add(restaurantFinal);
        UserHelper.updateUserRestaurantListFavorites(uid, restaurantList);


    }

    private void getCurrentUser()
    {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserHelper.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                currentUser = documentSnapshot.toObject(User.class);
            }
        });
    }

    @OnClick(R.id.details_fragment_website_button)
    void onClickWebsiteButton()
    {

    }

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(String placeId)
    {
        DetailsFragment detailsFragment = new DetailsFragment();
        //this.placeId = placeId;
        return detailsFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, v);
        placeId = DetailsActivity.placeId;
        this.restaurantFinal = stream(placeId);
        this.getCurrentUser();
        return v;
    }

    private Restaurant stream (String placeId)
    {
        String key = getActivity().getResources().getString(R.string.google_maps_key);

        this.disposable = RestaurantStreams.streamDetailRestaurantToRestaurant(placeId, key).subscribeWith(new DisposableObserver<Restaurant>() {
            @Override
            public void onNext(Restaurant restaurant)
            {
                restaurantFinal = restaurant;
                updateRestaurant(restaurantFinal);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        return restaurantFinal;
    }

    private void updateRestaurant(Restaurant restaurant)
    {
        name.setText(restaurant.getName());
        Glide.with(this).load(restaurant.getIllustration()).apply(RequestOptions.centerCropTransform()).into(illustration);
        address.setText(restaurant.getAddress());
        this.updateRating(restaurant);
    }

    private void updateRating(Restaurant restaurant)
    {

        double rating = restaurant.getRating();

        if (rating > 3.75)
        {
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.VISIBLE);
            star3.setVisibility(View.VISIBLE);

        }
        else if (rating > 2.5)
        {
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.VISIBLE);
            star3.setVisibility(View.INVISIBLE);

        }
        else if (rating > 1.25)
        {
            star1.setVisibility(View.VISIBLE);
            star2.setVisibility(View.INVISIBLE);
            star3.setVisibility(View.INVISIBLE);
        }
        else
        {
            star1.setVisibility(View.INVISIBLE);
            star2.setVisibility(View.INVISIBLE);
            star3.setVisibility(View.INVISIBLE);
        }

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
    public void onDestroy() {
        super.onDestroy();
        this.unsubscribe();
    }

}
