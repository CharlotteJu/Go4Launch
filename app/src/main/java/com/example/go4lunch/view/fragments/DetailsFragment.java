package com.example.go4lunch.view.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.RestaurantHelper;
import com.example.go4lunch.model.api.RestaurantStreams;
import com.example.go4lunch.model.api.UserHelper;
import com.example.go4lunch.view.activities.DetailsActivity;
import com.example.go4lunch.view.adapters.ListWorkmatesAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment implements ListWorkmatesAdapter.Listener {

    private String placeId;
    private Restaurant restaurantFinal;
    private Disposable disposable;
    private User currentUser;
    private String uidUser;
    private ListWorkmatesAdapter adapter;
    private final static int REQUEST_CODE = 13;

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
    @BindView(R.id.details_fragment_call_txt)
    TextView testCall;
    @BindView(R.id.details_fragment_workmates_recycler_view)
    RecyclerView workmatesRecyclerView;
    @BindView(R.id.details_fragment_choose_button)
    FloatingActionButton floatingActionButton;

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
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, v);
        placeId = DetailsActivity.placeId;
        this.restaurantFinal = stream(placeId);
        this.getCurrentUser();

        return v;
    }

    @OnClick(R.id.details_fragment_call_button)
    void onClickCallButton()
    {
        String phone = restaurantFinal.getPhoneNumber();
        Uri uri = Uri.parse("tel:"+phone);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
        }
        else
        {
            startActivity(intent);
        }

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

        if (!currentUser.getRestaurantListFavorites().contains(restaurantFinal))
        {
            restaurantList.add(restaurantFinal);
            UserHelper.updateUserRestaurantListFavorites(uidUser, restaurantList).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "Restaurant ajouté aux favoris", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            restaurantList.remove(restaurantFinal);
            UserHelper.updateUserRestaurantListFavorites(uidUser, restaurantList).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "Restaurant retiré des favoris", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getCurrentUser()
    {
        uidUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserHelper.getUser(uidUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
        String url = restaurantFinal.getWebsite();
        if(!url.startsWith("https://") && !url.startsWith("http://"))
        {
            url = "http://" + url;
        }
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
        {
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getContext(), "Aucune application correspondante trouvée", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.details_fragment_choose_button)
    void onClickChooseButton()
    {
        if(!this.currentUser.isChooseRestaurant())
        {
            this.currentUser.setRestaurantChoose(this.restaurantFinal);
            this.floatingActionButton.setImageResource(R.drawable.ic_choose_restaurant);
            UserHelper.updateUserRestaurant(uidUser, currentUser.getRestaurantChoose());
            UserHelper.updateUserIsChooseRestaurant(uidUser, currentUser.isChooseRestaurant());
        }
        else
        {
            this.currentUser.unSetRestaurantChoose();
            this.floatingActionButton.setImageResource(R.drawable.ic_choose_not_restaurant);
            UserHelper.updateUserRestaurant(uidUser, currentUser.getRestaurantChoose());
            UserHelper.updateUserIsChooseRestaurant(uidUser, currentUser.isChooseRestaurant());
        }


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

                //TODO voir la RCV
                //configRecyclerView();
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

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query)
    {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }


    /*private void configRecyclerView()
    {
        this.test();
        this.adapter = new ListWorkmatesAdapter(generateOptionsForAdapter(RestaurantHelper.getListWorkmates(restaurantFinal.getPlaceId())),
                    Glide.with(this), this, getActivity());
        this.workmatesRecyclerView.setAdapter(adapter);
        this.workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void test ()
    {
        RestaurantHelper.getRestaurant(restaurantFinal.getPlaceId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                RestaurantHelper.createRestaurant(restaurantFinal.getPlaceId(), restaurantFinal.getPlaceId(), new ArrayList<User>());
            }
        });
    }*/

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

    @Override
    public void onDataChanged()
    {

    }
}
