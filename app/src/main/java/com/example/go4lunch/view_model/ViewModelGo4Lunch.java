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


    private MutableLiveData<User> userCurrentMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<User>> usersListMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<Restaurant> restaurantFirebaseMutableLiveData= new MutableLiveData<>();
    private MutableLiveData<List<Restaurant>> restaurantsListFirebaseMutableLiveData= new MutableLiveData<>();

    private MutableLiveData<Observable<List<Restaurant>>> restaurantsListPlacesMutableLiveData= new MutableLiveData<>();
    private MutableLiveData<Restaurant> restaurantDetailPlacesMutableLiveData= new MutableLiveData<>();

    /////////////////////// USER FIREBASE ///////////////////////
    //-----------------------


    public MutableLiveData<User> getUserCurrentMutableLiveData(String uid)
    {
        if (this.userCurrentMutableLiveData != null)
        {
            this.setUserCurrentMutableLiveData(uid);
        }

        return this.userCurrentMutableLiveData;
    }

    private void setUserCurrentMutableLiveData(String uid)
    {
        this.userFirebaseRepository.getUser(uid).addOnSuccessListener(documentSnapshot ->
        {
            User user = documentSnapshot.toObject(User.class);
            userCurrentMutableLiveData.setValue(user);
        });
    }

    public MutableLiveData<List<User>> getUsersListMutableLiveData()
    {
        if (this.usersListMutableLiveData != null)
        {
            this.setUsersListMutableLiveData();
        }

        return this.usersListMutableLiveData;
    }

    private void setUsersListMutableLiveData()
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


    // PEUT-ON RECUPERER ONSUCCESS OU ONFAILURE ? --> CALLBACK (comme RCV)
    public void createUser (String uid, String email, String username, String urlPicture)
    {
        this.userFirebaseRepository.createUser(uid, email, username, urlPicture);
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

    public MutableLiveData<Restaurant> getRestaurantFirebaseMutableLiveData(String placeId)
    {
        if (this.restaurantFirebaseMutableLiveData != null)
        {
            this.setRestaurantFirebaseMutableLiveData(placeId);
        }

        return this.restaurantFirebaseMutableLiveData;
    }

    private void setRestaurantFirebaseMutableLiveData(String placeId)
    {
        this.restaurantFirebaseRepository.getRestaurant(placeId).addOnSuccessListener(documentSnapshot -> {
            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
            restaurantFirebaseMutableLiveData.setValue(restaurant);
        });
    }

    public MutableLiveData<List<Restaurant>> getRestaurantsListFirebaseMutableLiveData()
    {
        if (this.restaurantsListFirebaseMutableLiveData != null)
        {
            this.setRestaurantsListFirebaseMutableLiveData();
        }

        return this.restaurantsListFirebaseMutableLiveData;
    }

    private void setRestaurantsListFirebaseMutableLiveData()
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

    public MutableLiveData<Observable<List<Restaurant>>> getRestaurantsListPlacesMutableLiveData(double lat, double lng, int radius, String key)
    {
        if (this.restaurantsListPlacesMutableLiveData != null)
        {
            this.setRestaurantsListPlacesMutableLiveData(lat, lng, radius, key);
        }

        return this.restaurantsListPlacesMutableLiveData;
    }

    private void setRestaurantsListPlacesMutableLiveData(double lat, double lng, int radius, String key)
    {
        this.restaurantsListPlacesMutableLiveData.setValue(this.restaurantPlacesRepository.streamFetchRestaurantInList(lat, lng, radius, key));
    }

    public void setRestaurantDetailPlacesMutableLiveData(String placeId, String key)
    {
        //this.restaurantPlacesRepository.streamDetailRestaurantToRestaurant(placeId, key);
    }

}
