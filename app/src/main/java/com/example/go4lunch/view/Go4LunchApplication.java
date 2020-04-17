package com.example.go4lunch.view;

import androidx.multidex.MultiDexApplication;

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
