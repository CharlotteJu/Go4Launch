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

    public User getUser(String email)
    {
        UserHelper.getUser(email).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot)
            {
                currentUser = documentSnapshot.toObject(User.class);
            }
        });
        return currentUser;
    }

    public void createUser(String email, String username, String urlPicture)
    {
        UserHelper.createUser(email, username, urlPicture);
    }

    public void updateUserIsChooseRestaurant(String email, Boolean isRestaurantChoose)
    {
        UserHelper.updateUserIsChooseRestaurant(email, isRestaurantChoose);
    }

    public void updateUserRestaurant(String email, Restaurant restaurant)
    {
        UserHelper.updateUserRestaurant(email, restaurant);
    }

    public void updateUserRestaurantListFavorites(String email, List<Restaurant> restaurantListFavorites)
    {
        UserHelper.updateUserRestaurantListFavorites(email, restaurantListFavorites);
    }

    public void deleteUser (String email)
    {
        UserHelper.deleteUser(email);
    }





}
