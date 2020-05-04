package com.example.go4lunch.view_model.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.view_model.ViewModelGo4Lunch;
import com.example.go4lunch.view_model.repositories.RestaurantFirebaseRepository;
import com.example.go4lunch.view_model.repositories.RestaurantPlacesRepository;
import com.example.go4lunch.view_model.repositories.UserFirebaseRepository;

public class ViewModelFactoryGo4Lunch implements ViewModelProvider.Factory {

    private RestaurantFirebaseRepository restaurantFirebaseRepository;
    private UserFirebaseRepository userFirebaseRepository;
    private RestaurantPlacesRepository restaurantPlacesRepository;

    public ViewModelFactoryGo4Lunch(RestaurantFirebaseRepository restaurantFirebaseRepository,
                             UserFirebaseRepository userFirebaseRepository,
                             RestaurantPlacesRepository restaurantPlacesRepository) {
        this.restaurantFirebaseRepository = restaurantFirebaseRepository;
        this.userFirebaseRepository = userFirebaseRepository;
        this.restaurantPlacesRepository = restaurantPlacesRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)
    {
        if (modelClass.isAssignableFrom(ViewModelGo4Lunch.class))
        {
            return (T) new ViewModelGo4Lunch(this.restaurantFirebaseRepository, this.userFirebaseRepository, this.restaurantPlacesRepository);
        }
        throw new IllegalArgumentException("Problem with ViewModelFactory");
    }
}
