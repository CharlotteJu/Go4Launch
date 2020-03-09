package com.example.go4lunch.view.activities;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.go4lunch.R;
import com.google.android.gms.tasks.OnFailureListener;

public abstract class BaseActivity extends AppCompatActivity
{
    // Firebase Firestore
    /**
     * Creates an OnFailureListener object to check if Firebase has been send an error when the CRUD actions
     * @return an OnFailureListener object
     */
    protected OnFailureListener onFailureListener() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Creates a Toast object which displays a message
                Toast.makeText(getApplicationContext(), getString(R.string.failure), Toast.LENGTH_SHORT).show();
            }
        };
    }
}
