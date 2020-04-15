package com.example.go4lunch.view.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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
import com.example.go4lunch.view_model.ViewModelGo4Lunch;
import com.example.go4lunch.view_model.factory.ViewModelFactoryGo4Lunch;
import com.example.go4lunch.view_model.injection.Injection;
import com.example.go4lunch.utils.StaticFields;
import com.example.go4lunch.view.adapters.ListWorkmatesDetailsFragmentAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

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
    private List<Restaurant> restaurantsListFromFirebase;
    private ViewModelGo4Lunch viewModelGo4Lunch;

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

        this.configViewModel();
        //placeId = DetailsActivity.placeId;
        //currentUser = StaticFields.CURRENT_USER;
        //uidUser = StaticFields.IUD_USER;
        //this.restaurantFinal = stream(placeId);

        return v;
    }

    ////////////////////////////////////////// VIEW MODEL ///////////////////////////////////////////

    private void configViewModel()
    {
        ViewModelFactoryGo4Lunch viewModelFactoryGo4Lunch = Injection.viewModelFactoryGo4Lunch();
        viewModelGo4Lunch = ViewModelProviders.of(this, viewModelFactoryGo4Lunch).get(ViewModelGo4Lunch.class);
        this.getRestaurantFromPlaces();
       // this.getRestaurantFinalFromFirebase();
        //this.getCurrentUser();
       // this.getRestaurantListFromFirebase();
    }

    private void getRestaurantFromPlaces()
    {
        String key = BuildConfig.google_maps_key;
        this.viewModelGo4Lunch.getRestaurantDetailPlacesMutableLiveData(placeId, key)
                .observe(this, restaurantObservable -> {
                    disposable = restaurantObservable.subscribeWith(new DisposableObserver<Restaurant>() {
                        @Override
                        public void onNext(Restaurant restaurant)
                        {
                            restaurantFinal = restaurant;
                            getRestaurantListFromFirebase();
                           // updateRestaurant(restaurantFinal);
                        }

                        @Override
                        public void onError(Throwable e)
                        {
                            String error;
                        }

                        @Override
                        public void onComplete()
                        {
                            String complete;
                        }
                    });
        });
    }

    private void getRestaurantFinalFromFirebase()
    {
        this.viewModelGo4Lunch.getRestaurantFirebaseMutableLiveData(restaurantFinal)
                .observe(this, restaurant -> {
                    // S'il n'est pas dans la liste, est-ce que j'arrive ici ?
                    String test;
                    this.workmatesList = restaurant.getUserList();

                    restaurantFinal.setUserList(workmatesList);
                    this.configRecyclerView();


                    /*if (restaurant.getUserList() == null)
                    {
                        restaurantFinal.setUserList(new ArrayList<>());
                    }
                    else
                    {
                        restaurantFinal.setUserList(restaurant.getUserList());
                    }*/

                    //initFirebaseRestaurant();
                    getCurrentUser();
                    //updateRestaurant(restaurantFinal);

        });
    }

    private void getRestaurantListFromFirebase()
    {
        this.viewModelGo4Lunch.getRestaurantsListFirebaseMutableLiveData().observe(this, restaurantList -> {
            restaurantsListFromFirebase = restaurantList;
            if (!restaurantsListFromFirebase.contains(restaurantFinal)) {
                this.workmatesList = new ArrayList<>();
                this.viewModelGo4Lunch.createRestaurant(placeId, workmatesList, restaurantFinal.getName(), restaurantFinal.getAddress());
                //this.configRecyclerView();
                //restaurantFinal.setUserList(workmatesList);
            }
            this.getRestaurantFinalFromFirebase();

        });
    }

    private void initFirebaseRestaurant()
    {
        if (restaurantsListFromFirebase.contains(restaurantFinal))
        {
            this.workmatesList = restaurantFinal.getUserList();
            this.configRecyclerView();
        }
        else
        {
            this.workmatesList = new ArrayList<>();
            //this.viewModelGo4Lunch.createRestaurant(placeId, workmatesList, restaurantFinal.getName(), restaurantFinal.getAddress());
        }
    }



    private void getCurrentUser()
    {
        uidUser = FirebaseAuth.getInstance().getUid();
        this.viewModelGo4Lunch.getUserCurrentMutableLiveData(uidUser).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user)
            {
                currentUser = user;
                updateRestaurant(restaurantFinal);
            }
        });

    }

    ////////////////////////////////////////// ONCLICK ///////////////////////////////////////////

    @OnClick(R.id.details_fragment_call_button)
    void onClickCallButton()
    {
        if (restaurantFinal.getPhoneNumber() != null)
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
        else
        {
            Toast.makeText(getContext(), getResources().getString(R.string.details_fragment_no_phone), Toast.LENGTH_LONG).show();
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
        }
        else
        {
            restaurantList.remove(restaurantFinal);
        }

        this.viewModelGo4Lunch.updateUserRestaurantListFavorites(uidUser, restaurantList);
        this.updateLike();

        // Update data in Firebase
       /* if (!currentUser.getRestaurantListFavorites().contains(restaurantFinal))
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
        }*/


    }

    @OnClick(R.id.details_fragment_website_button)
    void onClickWebsiteButton()
    {
        if (restaurantFinal.getWebsite() != null)
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
        else
        {
            Toast.makeText(getContext(), getResources().getString(R.string.details_fragment_no_website), Toast.LENGTH_LONG).show();
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

            //UserFirebaseRepository.updateUserRestaurant(uidUser, currentUser.getRestaurantChoose());
            //UserFirebaseRepository.updateUserIsChooseRestaurant(uidUser, currentUser.isChooseRestaurant());
            workmatesList.add(currentUser);
            //RestaurantFirebaseRepository.updateRestaurantUserList(restaurantFinal.getPlaceId(), workmatesList);

            //StaticFields.RESTAURANT_CHOOSE_BY_CURRENT_USER = restaurantFinal;
            //StaticFields.RESTAURANT_CHOOSE_BY_CURRENT_USER.setUserList(workmatesList);
        }
        else
        {
            this.currentUser.unSetRestaurantChoose();
            this.floatingActionButton.setImageResource(R.drawable.ic_choose_not_restaurant);
            //UserFirebaseRepository.updateUserRestaurant(uidUser, currentUser.getRestaurantChoose());
            //UserFirebaseRepository.updateUserIsChooseRestaurant(uidUser, currentUser.isChooseRestaurant());

            workmatesList.remove(currentUser);

            //RestaurantFirebaseRepository.updateRestaurantUserList(restaurantFinal.getPlaceId(), workmatesList);
        }

        this.viewModelGo4Lunch.updateRestaurantUserList(restaurantFinal.getPlaceId(), workmatesList);
        this.viewModelGo4Lunch.updateUserRestaurant(uidUser, currentUser.getRestaurantChoose());
        this.viewModelGo4Lunch.updateUserIsChooseRestaurant(uidUser, currentUser.isChooseRestaurant());

        //this.getFirebaseRestaurant();

    }

    private void updateOtherRestaurantInFirebase(Restaurant restaurant)
    {
        if (restaurantsListFromFirebase.contains(restaurant))
        {
            int index = restaurantsListFromFirebase.indexOf(restaurant);
            List<User> tempListWorkmates = restaurantsListFromFirebase.get(index).getUserList();
            tempListWorkmates.remove(currentUser);
            this.viewModelGo4Lunch.updateRestaurantUserList(restaurant.getPlaceId(), tempListWorkmates);
        }
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

    private void updateRestaurant(Restaurant restaurant)
    {
        name.setText(restaurant.getName());
        Glide.with(this).load(restaurant.getIllustration()).apply(RequestOptions.centerCropTransform()).into(illustration);
        address.setText(restaurant.getAddress());
        this.configButton();
        this.updateRating(restaurant);
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

    ////////////////////////////////////////// ANCIENNES METHODES ///////////////////////////////////////////

    /*private void getCurrentUser()
    {
        uidUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        UserHelper.getUser(uidUser).addOnSuccessListener(documentSnapshot -> {
            currentUser = documentSnapshot.toObject(User.class);
            updateRestaurant(restaurantFinal);
        });
    }*/

    /*private void updateOtherRestaurantInFirebase(Restaurant restaurant)
    {
        if (restaurantsListFromFirebase.contains(restaurant))
        {
            int index = restaurantsListFromFirebase.indexOf(restaurant);
            List<User> tempListWorkmates = restaurantsListFromFirebase.get(index).getUserList();
            tempListWorkmates.remove(currentUser);
            this.viewModelGo4Lunch.updateRestaurantUserList(restaurant.getPlaceId(), tempListWorkmates);
        }

       /* RestaurantFirebaseRepository.getRestaurant(restaurant.getPlaceId()).addOnSuccessListener(documentSnapshot ->
        {
            if(documentSnapshot.exists())
            {
                List<User> tempWorkmatesList = Objects.requireNonNull(documentSnapshot.toObject(Restaurant.class)).getUserList();

                tempWorkmatesList.remove(currentUser);

                RestaurantFirebaseRepository.updateRestaurantUserList(restaurant.getPlaceId(),tempWorkmatesList);
            }
        });*/


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

        /*private Restaurant stream (String placeId)
    {
        String key = BuildConfig.google_maps_key;

        /*this.disposable = RestaurantPlacesRepository.streamDetailRestaurantToRestaurant(placeId, key).subscribeWith(new DisposableObserver<Restaurant>() {
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
    }*/

        /*private void getFirebaseRestaurant ()
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
        });*/



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
        });
    }*/


}
