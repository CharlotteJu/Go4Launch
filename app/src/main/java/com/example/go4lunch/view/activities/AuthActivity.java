package com.example.go4lunch.view.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.model.api.UserHelper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Charlotte Judon - 02/18
 * Activity for user authentication
 */
public class AuthActivity extends AppCompatActivity {

    private final static int FIREBASE_UI = 100;

    @Override
    protected void onStart() {
        super.onStart();
        this.userConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.auth_activity_google_button)
    void onClickGoogleButton()
    {
        this.startSignInWithGoogle();
    }

    @OnClick(R.id.auth_activity_facebook_button)
    void onClickFacebookButton()
    {
        this.startSignInWithFacebook();
    }

    private void userConnected()
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            String urlPicture = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            UserHelper.createUser(uid, email, name, urlPicture).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Création échouée", Toast.LENGTH_SHORT).show();
                }
            });
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    //UI

    private void startSignInWithGoogle()
    {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false, true)

                        .build(),FIREBASE_UI);
    }

    private void startSignInWithFacebook()
    {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)

                        .build(),FIREBASE_UI);
    }

    private void responseSignIn(int requestCode, int resultCode, Intent data)
    {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == FIREBASE_UI)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(getApplicationContext(), R.string.response_sign_in_success,Toast.LENGTH_SHORT ).show();
                this.userConnected();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(), R.string.response_sign_in_error,Toast.LENGTH_SHORT ).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.responseSignIn(requestCode, resultCode, data);
    }
}
