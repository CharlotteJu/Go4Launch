package com.example.go4lunch.notifications;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

public class NotificationWorkerTest
{
    private static Restaurant restaurant;
    private static User user_1;
    private static User user_2;
    private static List <String> message = new ArrayList<>();
    private static List<User> userList = new ArrayList<>();

    private static final String NAME_RESTAURANT = "Test Name Restaurant";
    private static final String ADDRESS_RESTAURANT = "Test Address Restaurant";
    private static final String USER_1 = "USER_1";
    private static final String USER_2 = "USER_2";
    private static final String WITH = "with";

    @BeforeClass
    public static void setUp()
    {

        restaurant = new Restaurant(NAME_RESTAURANT, ADDRESS_RESTAURANT);
        user_1 = new User(USER_1, USER_1, USER_1);
        user_2 = new User(USER_2, USER_2, USER_2);
        restaurant.setUserList(userList);

        message.add(NAME_RESTAURANT);
        message.add(ADDRESS_RESTAURANT);
    }

    @Before
    public void before()
    {
        userList.clear();
    }

    @Test
    public void buildMessageForNotification_WithoutWorkmates_Success()
    {
        List<String> messageTest = NotificationWorker.createMessage(restaurant, user_1, WITH);
        for (int i =0; i < messageTest.size(); i ++)
        {
            assertEquals(messageTest.get(i), message.get(i));
        }
    }

    @Test
    public void buildMessageForNotification_WithWorkmates_WithoutCurrentUser_Success()
    {
        userList.add(user_2);
        message.add("with " + user_2.getName());

        List<String> messageTest = NotificationWorker.createMessage(restaurant, user_1, WITH);
        for (int i =0; i < messageTest.size(); i ++)
        {
            assertEquals(messageTest.get(i), message.get(i));
        }
    }

    @Test
    public void buildMessageForNotification_WithWorkmates_WithCurrentUser_Success()
    {
        userList.add(user_2);
        userList.add(user_1);
        message.add("with " + user_2.getName());

        List<String> messageTest = NotificationWorker.createMessage(restaurant, user_1, WITH);
        for (int i =0; i < messageTest.size(); i ++)
        {
            assertEquals(messageTest.get(i), message.get(i));
        }
    }




}