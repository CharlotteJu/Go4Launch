package com.example.go4lunch.view_model.injection;


import com.example.go4lunch.view_model.factory.ViewModelFactoryGo4Lunch;
import com.example.go4lunch.view_model.repositories.RestaurantFirebaseRepository;
import com.example.go4lunch.view_model.repositories.RestaurantPlacesRepository;
import com.example.go4lunch.view_model.repositories.UserFirebaseRepository;

public class Injection
{

    private static RestaurantFirebaseRepository createRestaurantFirebaseRepository()
    {
        return new RestaurantFirebaseRepository();
    }

    private static UserFirebaseRepository createUserFirebaseRepository ()
    {
        return new UserFirebaseRepository();
    }

    private static RestaurantPlacesRepository createRestaurantPlacesRepository ()
    {
        return new RestaurantPlacesRepository();
    }

    public static ViewModelFactoryGo4Lunch viewModelFactoryGo4Lunch ()
    {
        return new ViewModelFactoryGo4Lunch(createRestaurantFirebaseRepository(), createUserFirebaseRepository(), createRestaurantPlacesRepository());
    }

}
