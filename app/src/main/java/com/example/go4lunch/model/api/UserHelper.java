package com.example.go4lunch.model.api;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserHelper
{
    public static CollectionReference getCollectionUser ()
    {
        return FirebaseFirestore.getInstance().collection("user");
    }

    public static Task<Void> createUser(String email, String username, String urlPicture)
    {
        User toCreate = new User(email, username, urlPicture);
        return UserHelper.getCollectionUser().document(email).set(toCreate);
    }

    public static Task<DocumentSnapshot> getUser(String email, String username, String urlPicture)
    {
        return UserHelper.getCollectionUser().document(email).get();
    }

    public static Task<Void> updateUserIsChooseRestaurant(String email, Boolean isChooseRestaurant)
    {
        return UserHelper.getCollectionUser().document(email).update("isChooseRestaurant", isChooseRestaurant);
    }

    public static Task<Void> updateUserIsChooseRestaurant(String email, Restaurant restaurantChoose)
    {
        return UserHelper.getCollectionUser().document(email).update("restaurantChoose", restaurantChoose);
    }

    public static Task<Void> updateUserrestaurantListFavorites(String email, List<Restaurant> restaurantListFavorites)
    {
        return UserHelper.getCollectionUser().document(email).update("restaurantListFavorites", restaurantListFavorites);
    }

    public static Task<Void> deleteUser(String email, String username, String urlPicture)
    {
        return UserHelper.getCollectionUser().document(email).delete();
    }




}
