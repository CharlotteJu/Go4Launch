package com.example.go4lunch.view.activities;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import static androidx.test.espresso.action.ViewActions.click;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import com.example.go4lunch.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.junit.Assert.*;

public class MainActivityTest
{
    @Rule
    public ActivityTestRule activityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void bottomNavigationView_clickListRestaurants_DisplayGoodFragment()
    {
        Espresso.onView(ViewMatchers.withId(R.id.action_listview)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.fragment_list_restaurants_constraint_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void bottomNavigationView_clickListWorkmates_DisplayGoodFragment()
    {
        Espresso.onView(ViewMatchers.withId(R.id.action_workmates)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.fragment_list_workmates_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void bottomNavigationView_clickMap_DisplayGoodFragment()
    {
        Espresso.onView(ViewMatchers.withId(R.id.action_mapview)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.map)).check(matches(isDisplayed()));
    }



}