package com.example.go4lunch.view.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.view_model.ViewModelGo4Lunch;
import com.example.go4lunch.view_model.factory.ViewModelFactoryGo4Lunch;
import com.example.go4lunch.view_model.injection.Injection;
import com.example.go4lunch.view_model.repositories.UserFirebaseRepository;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Charlotte Judon - 02/18
 * Activity for user authentication
 */
public class AuthActivity extends AppCompatActivity {

    private final static int FIREBASE_UI = 100;
    private Boolean userExists = false;

    private ViewModelGo4Lunch viewModelGo4Lunch;
    private List<User> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        this.configViewModel();
    }


    private void configViewModel()
    {
        ViewModelFactoryGo4Lunch viewModelFactoryGo4Lunch = Injection.viewModelFactoryGo4Lunch();
        viewModelGo4Lunch = ViewModelProviders.of(this, viewModelFactoryGo4Lunch).get(ViewModelGo4Lunch.class);
        this.getUsersList();
    }

    private void getUsersList()
    {
        this.viewModelGo4Lunch.getUsersListMutableLiveData().observe(this, userList -> {
            usersList = userList;
            connectUser();
        });
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

    private void connectUser()
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            for (int i = 0; i < usersList.size(); i ++)
            {
                if (usersList.get(i).getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                {
                    userExists = true;
                    break;
                }
            }
            if (userExists)
            {
                lunchMainActivity();
            }
            else
            {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String urlPicture = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();

                viewModelGo4Lunch.createUser(uid, email, name, urlPicture);
                this.lunchMainActivity();
            }
        }
    }

    private void lunchMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
        //TODO : FAIRE UN TRUC DE RESPONSE ?
        if (requestCode == FIREBASE_UI)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.response_sign_in_success),Toast.LENGTH_SHORT ).show();
                this.connectUser();
            }
            else
            {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.response_sign_in_error),Toast.LENGTH_SHORT ).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.responseSignIn(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {}
}
