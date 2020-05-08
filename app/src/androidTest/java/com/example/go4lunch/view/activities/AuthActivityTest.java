package com.example.go4lunch.view.activities;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAssertion;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.example.go4lunch.R;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthActivityTest
{
    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule<>(AuthActivity.class);

    @Test
    public void authActivity_GoogleButton_isDisplayed()
    {
        Espresso.onView(ViewMatchers.withId(R.id.auth_activity_google_button)).check(matches(isDisplayed()));
    }

    @Test
    public void authActivity_FacebookButton_isDisplayed()
    {
        Espresso.onView(ViewMatchers.withId(R.id.auth_activity_facebook_button)).check(matches(isDisplayed()));
    }
}