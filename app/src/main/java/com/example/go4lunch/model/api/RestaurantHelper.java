package com.example.go4lunch.model.api;

import com.example.go4lunch.model.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantHelper
{
    public static CollectionReference getCollectionRestaurant()
    {
        return FirebaseFirestore.getInstance().collection("restaurant");
    }

   /* public static Task<Void> createRestaurant(String name, String type, String address, int hours, String urlPicture)
    {
        Restaurant toCreate = new Restaurant(name, type, address, hours, urlPicture);
        return RestaurantHelper.getCollectionRestaurant().document(name).set(toCreate);
    }*/

    public static Task<DocumentSnapshot> getRestaurant(String name)
    {
        return RestaurantHelper.getCollectionRestaurant().document(name).get();
    }

    public static Task<Void> updateRestaurantHours(String name, int hours)
    {
        return RestaurantHelper.getCollectionRestaurant().document(name).update("hours", hours);
    }

    public static Task<Void> deleteRestaurant(String name)
    {
        return RestaurantHelper.getCollectionRestaurant().document(name).delete();
    }


}
