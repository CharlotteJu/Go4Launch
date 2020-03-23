package com.example.go4lunch.view_model;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.UserHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.util.Listener;

import java.util.List;

public class UserRepository
{
    private final UserHelper userHelper;
    private User currentUser;

    public UserRepository(UserHelper userHelper) {
        this.userHelper = userHelper;
    }

    public User getUser(String uid)
    {
        UserHelper.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                currentUser = documentSnapshot.toObject(User.class);
            }
        });
        return currentUser;
    }

    public void createUser(String uid, String username, String urlPicture)
    {
        //UserHelper.createUser(uid, username, urlPicture);
    }

    public void updateUserIsChooseRestaurant(String uid, Boolean isRestaurantChoose)
    {
        UserHelper.updateUserIsChooseRestaurant(uid, isRestaurantChoose);
    }

    public void updateUserRestaurant(String uid, Restaurant restaurant)
    {
        UserHelper.updateUserRestaurant(uid, restaurant);
    }

    public void updateUserRestaurantListFavorites(String uid, List<Restaurant> restaurantListFavorites)
    {
        UserHelper.updateUserRestaurantListFavorites(uid, restaurantListFavorites);
    }

    public void deleteUser (String uid)
    {
        UserHelper.deleteUser(uid);
    }





}
