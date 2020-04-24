package com.example.go4lunch.view.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import java.util.Objects;

import butterknife.BindView;
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

    @BindView(R.id.progress_bar_layout)
    ConstraintLayout progressBarLayout;
    @BindView(R.id.auth_activity_facebook_button)
    Button facebookButton;
    @BindView(R.id.auth_activity_google_button)
    Button googleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        this.showProgressBar();
        this.configViewModel();
    }

    ///////////////////////////////////VIEW MODEL///////////////////////////////////

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

    ///////////////////////////////////ON CLICK///////////////////////////////////

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

    ///////////////////////////////////SIGN IN///////////////////////////////////

    /**
     * Sign in With Google
     */
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
        this.getUsersList();
    }

    /**
     * Sign in With Facebook
     */
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
        this.getUsersList();
    }

    ///////////////////////////////////UI///////////////////////////////////

    private void showProgressBar()
    {
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            this.progressBarLayout.setVisibility(View.VISIBLE);
            this.facebookButton.setVisibility(View.INVISIBLE);
            this.googleButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Connect the User
     * If he exists in Firebase, just connect
     * If he not exists in Firebase, create him
     */
    private void connectUser()
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(usersList != null)
            {
                int size = usersList.size();
                for (int i = 0; i < size; i ++)
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
                    String urlPicture = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).toString();

                    viewModelGo4Lunch.createUser(uid, email, name, urlPicture);
                    this.lunchMainActivity();
                }
            }
        }
        else
        {
            this.progressBarLayout.setVisibility(View.INVISIBLE);
            this.facebookButton.setVisibility(View.VISIBLE);
            this.googleButton.setVisibility(View.VISIBLE);
        }
    }

    private void lunchMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

    /**
     * Suppress super because we don't want that the User can press Back
     */
    @Override
    public void onBackPressed() {}
}
