package com.example.go4lunch.view;

import androidx.multidex.MultiDexApplication;

import com.example.go4lunch.model.User;
import com.example.go4lunch.view_model.repositories.UserFirebaseRepository;
import com.example.go4lunch.utils.StaticFields;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Go4LunchApplication extends MultiDexApplication
{



    @Override
    public void onCreate() {
        super.onCreate();
        //this.getCurrentUser();
    }



   /* private void getCurrentUser()
    {
        String uidUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        UserFirebaseRepository.getUser(uidUser).addOnSuccessListener(documentSnapshot -> {
            StaticFields.CURRENT_USER = documentSnapshot.toObject(User.class);
            StaticFields.IUD_USER = uidUser;
        });
    }*/
}
