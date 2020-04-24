package com.example.go4lunch.view_model.repositories;

import com.example.go4lunch.model.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

public interface UserFirebaseInterface
{
    CollectionReference getCollectionUser ();
    Query getListUsers();
    Task<Void> createUser(String uid, String email, String username, String urlPicture);
    Task<DocumentSnapshot> getUser(String uid);
    Task<Void> updateUserIsChooseRestaurant(String uid, Boolean isChooseRestaurant);
    Task<Void> updateUserRestaurant(String uid, Restaurant restaurantChoose);
    Task<Void> updateUserRestaurantListFavorites(String uid, List<Restaurant> restaurantListFavorites);

}
