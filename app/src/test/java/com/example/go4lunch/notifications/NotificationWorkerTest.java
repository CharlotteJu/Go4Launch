package com.example.go4lunch.notifications;

import android.content.Context;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

public class NotificationWorkerTest
{
    private static Context context = mock(Context.class);
    private static Restaurant restaurant = mock(Restaurant.class);
    private static User user = mock(User.class);
    private NotificationWorker notificationWorker = mock(NotificationWorker.class);

    @BeforeClass
    public static void setUp()
    {
        doNothing().when(user).setName("Charlotte");
        doNothing().when(user).setIllustration("Charlotte");

        doNothing().when(restaurant).setName("Test Name Restaurant");
        doNothing().when(restaurant).setAddress("Test Address Restaurant");
        doNothing().when(restaurant).setUserList(new ArrayList<>());
    }

    @Test
    public void buildMessageForNotification_WithoutWorkmates_Success()
    {
        // TODO : Tester une méthode privée ?
    }

}