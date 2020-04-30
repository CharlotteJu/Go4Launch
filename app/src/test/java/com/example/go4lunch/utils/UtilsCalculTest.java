package com.example.go4lunch.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

public class UtilsCalculTest
{
    @Spy
    private LatLng latLngRight = new LatLng(48.8376158, 2.2303507);
    @Spy
    private LatLng latLngLeft = new LatLng(48.8376158, 2.2303507);
    @Spy
    private static Location currentLocation = mock(Location.class);
    @Spy
    private List<LatLng> list = new ArrayList<>();
    @Spy
    private double radius;

    @BeforeClass
    public static void setUp()
    {
        currentLocation.setLatitude(48.8376158);
        currentLocation.setLongitude(2.2303507);
    }

    @Test
    public void calculateRadiusSinceCurrentLocation_0_Success()
    {
        when(UtilsCalcul.calculateRadiusAccordingToCurrentLocation(latLngRight, latLngLeft, currentLocation)).thenReturn(0.0);
    }

    @Test
    public void calculateRectangularBoundsSinceCurrentLocation_0_Success()
    {
        radius = 0;


        list.add(latLngRight);
        list.add(latLngLeft);

        //UtilsCalcul utilsCalcul = mock(UtilsCalcul.class);
        //spy(utilsCalcul);
        //List listTest = utilsCalcul.calculateRectangularBoundsAccordingToCurrentLocation(radius, currentLocation);
        //when(UtilsCalcul.calculateRectangularBoundsAccordingToCurrentLocation(radius, currentLocation)).thenReturn(list);

        //doNothing().when(UtilsCalcul.calculateRectangularBoundsAccordingToCurrentLocation(radius, currentLocation));
        //doReturn(list).when(UtilsCalcul.calculateRectangularBoundsAccordingToCurrentLocation(radius, currentLocation));
        //List listTest = UtilsCalcul.calculateRectangularBoundsAccordingToCurrentLocation(radius, currentLocation);
       // assertEquals(listTest.get(0), list.get(0));
       // assertEquals(listTest.get(1), list.get(1));

        // TODO : Faire Assert sur les listes

       //when(UtilsCalcul.calculateRectangularBoundsAccordingToCurrentLocation(radius, currentLocation)).thenReturn(list);
       //spy(utilsCalcul);
       //when(UtilsCalcul.calculateRectangularBoundsAccordingToCurrentLocation(radius, currentLocation)).thenReturn(list);

        Location currentLocation2 = new Location("TEST");
        currentLocation2.setLatitude(48.8376158);
        currentLocation2.setLongitude(2.2303507);

        List <LatLng> listTest = new ArrayList<>();
        listTest.add(latLngRight);
        listTest.add(latLngLeft);

        List<LatLng> forTest = UtilsCalcul.calculateRectangularBoundsAccordingToCurrentLocation(radius, currentLocation);
        assertEquals(listTest.get(0), forTest.get(0));
        assertEquals(listTest.get(1), forTest.get(1));

    }



}