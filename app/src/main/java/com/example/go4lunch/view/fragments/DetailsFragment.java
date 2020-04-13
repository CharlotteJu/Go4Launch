package com.example.go4lunch.view.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

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
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.view_model.repositories.RestaurantFirebaseRepository;
import com.example.go4lunch.view_model.repositories.RestaurantPlacesRepository;
import com.example.go4lunch.view_model.repositories.UserFirebaseRepository;
import com.example.go4lunch.utils.StaticFields;
import com.example.go4lunch.view.adapters.ListWorkmatesDetailsFragmentAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    // FOR DATA
    private String placeId;
    private Restaurant restaurantFinal;
    private Disposable disposable;
    private User currentUser;
    private String uidUser;
    private ListWorkmatesDetailsFragmentAdapter adapter;
    private List<User> workmatesList;

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
    @BindView(R.id.details_fragment_like_button)
    ImageView likeButton;
    @BindView(R.id.details_fragment_call_txt)
    TextView testCall;
    @BindView(R.id.details_fragment_workmates_recycler_view)
    RecyclerView workmatesRecyclerView;
    @BindView(R.id.details_fragment_choose_button)
    FloatingActionButton floatingActionButton;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public DetailsFragment(String placeId) {
        this.placeId = placeId;
    }

    public static DetailsFragment newInstance(String placeId)
    {
        DetailsFragment detailsFragment = new DetailsFragment(placeId);
        return detailsFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, v);
        //placeId = DetailsActivity.placeId;
        currentUser = StaticFields.CURRENT_USER;
        uidUser = StaticFields.IUD_USER;
        this.restaurantFinal = stream(placeId);

        return v;
    }

    @OnClick(R.id.details_fragment_call_button)
    void onClickCallButton()
    {
        String phone = restaurantFinal.getPhoneNumber();
        Uri uri = Uri.parse("tel:"+phone);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);

        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
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

        // Update data in Firebase
        if (!currentUser.getRestaurantListFavorites().contains(restaurantFinal))
        {
            restaurantList.add(restaurantFinal);
            UserFirebaseRepository.updateUserRestaurantListFavorites(uidUser, restaurantList).addOnSuccessListener
                    (aVoid -> Toast.makeText(getContext(), getResources().getString(R.string.details_fragment_restaurant_added_favorites), Toast.LENGTH_SHORT).show());
        }
        else
        {
            restaurantList.remove(restaurantFinal);
            UserFirebaseRepository.updateUserRestaurantListFavorites(uidUser, restaurantList).addOnSuccessListener
                    (aVoid -> Toast.makeText(getContext(), getResources().getString(R.string.details_fragment_restaurant_removed_favorites), Toast.LENGTH_SHORT).show());
        }

        this.updateLike();
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

        if (intent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null)
        {
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getContext(), getResources().getString(R.string.details_fragment_website_no_application_found), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.details_fragment_choose_button)
    void onClickChooseButton()
    {

        if(!this.currentUser.isChooseRestaurant() || !this.currentUser.getRestaurantChoose().equals(restaurantFinal))
        {
            if (this.currentUser.isChooseRestaurant())
            {
                this.updateOtherRestaurantInFirebase(currentUser.getRestaurantChoose());
            }

            this.currentUser.setRestaurantChoose(this.restaurantFinal);
            this.floatingActionButton.setImageResource(R.drawable.ic_choose_restaurant);
            UserFirebaseRepository.updateUserRestaurant(uidUser, currentUser.getRestaurantChoose());
            UserFirebaseRepository.updateUserIsChooseRestaurant(uidUser, currentUser.isChooseRestaurant());
            workmatesList.add(currentUser);
            RestaurantFirebaseRepository.updateRestaurantUserList(restaurantFinal.getPlaceId(), workmatesList);

            StaticFields.RESTAURANT_CHOOSE_BY_CURRENT_USER = restaurantFinal;
            StaticFields.RESTAURANT_CHOOSE_BY_CURRENT_USER.setUserList(workmatesList);
        }
        else
        {
            this.currentUser.unSetRestaurantChoose();
            this.floatingActionButton.setImageResource(R.drawable.ic_choose_not_restaurant);
            UserFirebaseRepository.updateUserRestaurant(uidUser, currentUser.getRestaurantChoose());
            UserFirebaseRepository.updateUserIsChooseRestaurant(uidUser, currentUser.isChooseRestaurant());

            workmatesList.remove(currentUser);

            RestaurantFirebaseRepository.updateRestaurantUserList(restaurantFinal.getPlaceId(), workmatesList);
        }

        this.getFirebaseRestaurant();

    }

    private void updateOtherRestaurantInFirebase(Restaurant restaurant)
    {

        RestaurantFirebaseRepository.getRestaurant(restaurant.getPlaceId()).addOnSuccessListener(documentSnapshot ->
        {
            if(documentSnapshot.exists())
            {
                List<User> tempWorkmatesList = Objects.requireNonNull(documentSnapshot.toObject(Restaurant.class)).getUserList();

                tempWorkmatesList.remove(currentUser);

                RestaurantFirebaseRepository.updateRestaurantUserList(restaurant.getPlaceId(),tempWorkmatesList);
            }
        });


        ////////////////////////////// METHODE UPDATE LISTE AVEC SNAPSHOT LISTENER //////////////////////////////

        /*RestaurantHelper.getListRestaurants().addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null)
            {
                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i ++)
                {
                    if (Objects.equals(queryDocumentSnapshots.getDocuments().get(i).get("placeId"), restaurant.getPlaceId()))
                    {
                        String debug1 = (String) queryDocumentSnapshots.getDocuments().get(i).get("placeId");
                        String debug2 = restaurant.getPlaceId();

                        String uid = queryDocumentSnapshots.getDocuments().get(i).getId();
                        RestaurantHelper.getRestaurant(uid).addOnSuccessListener(documentSnapshot -> {

                            List<User> tempWorkmatesList = Objects.requireNonNull(documentSnapshot.toObject(Restaurant.class)).getUserList();

                            tempWorkmatesList.remove(currentUser);

                            RestaurantHelper.updateRestaurantUserList(uid,tempWorkmatesList);
                        });
                        break;
                    }
                }
            }
        });*/
    }

    private void configButton()
    {
        if (!this.currentUser.isChooseRestaurant() || !this.currentUser.getRestaurantChoose().equals(this.restaurantFinal))
        {
            this.floatingActionButton.setImageResource(R.drawable.ic_choose_not_restaurant);
        }
        else
        {
            this.floatingActionButton.setImageResource(R.drawable.ic_choose_restaurant);
        }
    }

    private Restaurant stream (String placeId)
    {
        String key = BuildConfig.google_maps_key;

        this.disposable = RestaurantPlacesRepository.streamDetailRestaurantToRestaurant(placeId, key).subscribeWith(new DisposableObserver<Restaurant>() {
            @Override
            public void onNext(Restaurant restaurant)
            {
                restaurantFinal = restaurant;
                updateRestaurant(restaurantFinal);
                //getCurrentUser();
            }

            @Override
            public void onError(Throwable e) {}

            @Override
            public void onComplete() {}
        });

        return restaurantFinal;
    }

    /*private void getCurrentUser()
    {
        uidUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        UserHelper.getUser(uidUser).addOnSuccessListener(documentSnapshot -> {
            currentUser = documentSnapshot.toObject(User.class);
            updateRestaurant(restaurantFinal);
        });
    }*/

    private void getFirebaseRestaurant ()
    {
        RestaurantFirebaseRepository.getRestaurant(restaurantFinal.getPlaceId()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
            {
                workmatesList = Objects.requireNonNull(documentSnapshot.toObject(Restaurant.class)).getUserList();
                configRecyclerView();
            }
            else
            {
                workmatesList = new ArrayList<>();
                RestaurantFirebaseRepository.createRestaurant(restaurantFinal.getPlaceId(), restaurantFinal.getPlaceId(), workmatesList, restaurantFinal.getName(), restaurantFinal.getAddress());
            }
        });



        /*RestaurantHelper.getListRestaurants().addSnapshotListener(Objects.requireNonNull(getActivity()), (queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null)
            {
                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i ++)
                {
                    if (Objects.equals(queryDocumentSnapshots.getDocuments().get(i).get("placeId"), restaurantFinal.getPlaceId()))
                    {
                        uidRestaurant = queryDocumentSnapshots.getDocuments().get(i).getId();
                        restaurantExistsInFirebase = true;
                        RestaurantHelper.getRestaurant(uidRestaurant).addOnSuccessListener(documentSnapshot -> {
                            workmatesList = Objects.requireNonNull(documentSnapshot.toObject(Restaurant.class)).getUserList();
                            configRecyclerView();
                        });
                        break;
                    }
                }

                if (!restaurantExistsInFirebase)
                {
                    //uidRestaurant = UUID.randomUUID().toString();
                    RestaurantHelper.createRestaurant(restaurantFinal.getPlaceId(), restaurantFinal.getPlaceId(), new ArrayList<User>(), restaurantFinal.getName());
                }
            }
        });*/
    }



    private void updateRestaurant(Restaurant restaurant)
    {
        name.setText(restaurant.getName());
        Glide.with(this).load(restaurant.getIllustration()).apply(RequestOptions.centerCropTransform()).into(illustration);
        address.setText(restaurant.getAddress());
        this.configButton();
        this.updateRating(restaurant);
        this.getFirebaseRestaurant();
        this.updateLike();
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

    private void updateLike()
    {
        boolean favorite = false;

        if (currentUser.getRestaurantListFavorites() != null)
        {
            if (currentUser.getRestaurantListFavorites().contains(restaurantFinal))
            {
                favorite = true;
            }
        }

        if (favorite)
        {
            this.likeButton.setImageResource(R.drawable.ic_star_yellow_24dp);
        }
        else
        {
            this.likeButton.setImageResource(R.drawable.ic_star_orange_24dp);
        }
    }

    private void configRecyclerView()
    {

        this.adapter = new ListWorkmatesDetailsFragmentAdapter(workmatesList, Glide.with(this), getActivity());
        this.workmatesRecyclerView.setAdapter(adapter);
        this.workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
