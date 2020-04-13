package com.example.go4lunch.view_model.repositories;

import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

public interface RestaurantFirebaseInterface
{
    CollectionReference getCollectionRestaurant();

    Task<Void> createRestaurant(String uid, String placeId, List<User> userList, String name, String address);

    Task<DocumentSnapshot> getRestaurant(String uid);

    Query getListRestaurants();

    Task<Void> updateRestaurantUserList(String uid, List<User> userList);

}
