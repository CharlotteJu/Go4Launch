package com.example.go4lunch.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.view_model.repositories.RestaurantFirebaseRepository;
import com.example.go4lunch.view_model.repositories.RestaurantPlacesRepository;
import com.example.go4lunch.view_model.repositories.UserFirebaseRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class ViewModelGo4Lunch extends ViewModel
{
    private RestaurantFirebaseRepository restaurantFirebaseRepository;
    private UserFirebaseRepository userFirebaseRepository;
    private RestaurantPlacesRepository restaurantPlacesRepository;

    public ViewModelGo4Lunch(RestaurantFirebaseRepository restaurantFirebaseRepository,
                             UserFirebaseRepository userFirebaseRepository,
                             RestaurantPlacesRepository restaurantPlacesRepository) {
        this.restaurantFirebaseRepository = restaurantFirebaseRepository;
        this.userFirebaseRepository = userFirebaseRepository;
        this.restaurantPlacesRepository = restaurantPlacesRepository;
    }


    public MutableLiveData<User> userCurrentMutableLiveData;
    public MutableLiveData<List<User>> usersListMutableLiveData;

    public MutableLiveData<Restaurant> restaurantFirebaseMutableLiveData;
    public MutableLiveData<List<Restaurant>> restaurantsListFirebaseMutableLiveData;

    public MutableLiveData<Observable<List<Restaurant>>> restaurantsListPlacesMutableLiveData;
    public MutableLiveData<Restaurant> restaurantDetailPlacesMutableLiveData;

    /////////////////////// USER FIREBASE ///////////////////////
    //-----------------------



    public void setUserCurrentMutableLiveData(String uid)
    {
        this.userFirebaseRepository.getUser(uid).addOnSuccessListener(documentSnapshot ->
        {
            User user = documentSnapshot.toObject(User.class);
            userCurrentMutableLiveData.setValue(user);
        });
    }

    public void setUsersListMutableLiveData()
    {
        this.userFirebaseRepository.getListUsers().addSnapshotListener((queryDocumentSnapshots, e) ->
        {
            assert queryDocumentSnapshots != null;
            List<DocumentSnapshot> userList = queryDocumentSnapshots.getDocuments();
            List<User> users = new ArrayList<>();

            for (int i = 0; i < userList.size(); i ++)
            {
                User user = userList.get(i).toObject(User.class);
                users.add(user);
            }

            usersListMutableLiveData.setValue(users);
        });
    }

    public void getListUser()
    {
        this.userFirebaseRepository.getListUsers();
    }

    // PEUT-ON RECUPERER ONSUCCESS OU ONFAILURE ? --> CALLBACK (comme RCV)
    public boolean createUser (String uid, String email, String username, String urlPicture)
    {
        final boolean[] userCreated = new boolean[1];

        this.userFirebaseRepository.createUser(uid, email, username, urlPicture)
                .addOnSuccessListener(aVoid -> userCreated[0] = true)
                .addOnFailureListener(e -> userCreated[0] = false);

        return userCreated[0];
    }

    public void updateUserIsChooseRestaurant (String uid, Boolean isChooseRestaurant)
    {
        this.userFirebaseRepository.updateUserIsChooseRestaurant(uid, isChooseRestaurant);
    }

    public void updateUserRestaurant(String uid, Restaurant restaurantChoose)
    {
        this.userFirebaseRepository.updateUserRestaurant(uid, restaurantChoose);
    }

    public void updateUserRestaurantListFavorites(String uid, List<Restaurant> restaurantList)
    {
        this.userFirebaseRepository.updateUserRestaurantListFavorites(uid, restaurantList);
    }

    /////////////////////// RESTAURANT FIREBASE ///////////////////////
    //-----------------------

    public void setRestaurantFirebaseMutableLiveData(String uid)
    {
        this.restaurantFirebaseRepository.getRestaurant(uid).addOnSuccessListener(documentSnapshot -> {
            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
            restaurantFirebaseMutableLiveData.setValue(restaurant);
        });
    }

    public void setRestaurantsListFirebaseMutableLiveData()
    {
        this.restaurantFirebaseRepository.getListRestaurants().addSnapshotListener((queryDocumentSnapshots, e) ->
        {
            assert queryDocumentSnapshots != null;
            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();

            List<Restaurant> restaurantList = new ArrayList<>();

            for (int i = 0; i < documents.size(); i ++)
            {
                Restaurant restaurant = documents.get(i).toObject(Restaurant.class);
                restaurantList.add(restaurant);
            }

            restaurantsListFirebaseMutableLiveData.setValue(restaurantList);
        });
    }

    public void createRestaurant(String uid, String placeId, List<User> userList, String name, String address)
    {
        this.restaurantFirebaseRepository.createRestaurant(uid, placeId, userList, name, address);
    }

    public void updateRestaurantUserList(String uid, List<User> userList)
    {
        this.restaurantFirebaseRepository.updateRestaurantUserList(uid, userList);
    }

    /////////////////////// RESTAURANT PLACES ///////////////////////
    //-----------------------

    // COMMENT RECUPERER UN OBSERVABLE DANS UN LIVEDATA ?
    public void setRestaurantsListPlacesMutableLiveData(double lat, double lng, int radius, String key)
    {
        this.restaurantsListPlacesMutableLiveData.setValue(this.restaurantPlacesRepository.streamFetchRestaurantInList(lat, lng, radius, key));
    }

    public void setRestaurantDetailPlacesMutableLiveData(String placeId, String key)
    {
        //this.restaurantPlacesRepository.streamDetailRestaurantToRestaurant(placeId, key);
    }

}
