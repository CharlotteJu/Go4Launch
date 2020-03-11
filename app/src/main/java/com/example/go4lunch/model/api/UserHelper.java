package com.example.go4lunch.model.api;

import android.icu.text.AlphabeticIndex;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class UserHelper
{
    public static CollectionReference getCollectionUser ()
    {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static Query getListUsers()
    {
        return UserHelper.getCollectionUser().orderBy("name");
    }

    public static Task<Void> createUser(String uid, String email, String username, String urlPicture)
    {
        User toCreate = new User(email, username, urlPicture);
        return UserHelper.getCollectionUser().document(uid).set(toCreate);
    }

    public static Task<DocumentSnapshot> getUser(String email)
    {
        return UserHelper.getCollectionUser().document(email).get();
    }

    public static Task<Void> updateUserIsChooseRestaurant(String email, Boolean isChooseRestaurant)
    {
        return UserHelper.getCollectionUser().document(email).update("isChooseRestaurant", isChooseRestaurant);
    }

    public static Task<Void> updateUserRestaurant(String email, Restaurant restaurantChoose)
    {
        return UserHelper.getCollectionUser().document(email).update("restaurantChoose", restaurantChoose);
    }

    public static Task<Void> updateUserRestaurantListFavorites(String email, List<Restaurant> restaurantListFavorites)
    {
        return UserHelper.getCollectionUser().document(email).update("restaurantListFavorites", restaurantListFavorites);
    }

    public static Task<Void> deleteUser(String email)
    {
        return UserHelper.getCollectionUser().document(email).delete();
    }




}
