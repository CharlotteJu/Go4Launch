package com.example.go4lunch.model.api;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class RestaurantHelper
{
    public static CollectionReference getCollectionRestaurant()
    {
        return FirebaseFirestore.getInstance().collection("restaurant");
    }

    public static Task<Void> createRestaurant(String uid, String placeId, List<User> userList, String name, String address)
    {
        Restaurant toCreate = new Restaurant(placeId, userList, name, address);
        return RestaurantHelper.getCollectionRestaurant().document(uid).set(toCreate);
    }

    public static Task<DocumentSnapshot> getRestaurant(String uid)
    {
        return RestaurantHelper.getCollectionRestaurant().document(uid).get();
    }

    public static Query getListRestaurants()
    {
        return RestaurantHelper.getCollectionRestaurant().orderBy("name");
    }

    public static Query getWorkmatesListForARestaurant(String uid)
    {
        return RestaurantHelper.getCollectionRestaurant().document(uid).collection("userList");
    }

    public static Task<Void> updateRestaurantUserList(String uid, List<User> userList)
    {
        return RestaurantHelper.getCollectionRestaurant().document(uid).update("userList", userList);
    }

    public static Task<Void> deleteRestaurant(String uid)
    {
        return RestaurantHelper.getCollectionRestaurant().document(uid).delete();
    }


}
