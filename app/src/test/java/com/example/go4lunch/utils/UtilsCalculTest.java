package com.example.go4lunch.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

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

        //doReturn(list).when(UtilsCalcul.class).calculateRectangularBoundsSinceCurrentLocation(radius, currentLocation));

       //doReturn(list).when(utilsCalcul).calculateRectangularBoundsSinceCurrentLocation(radius, currentLocation);

        when(UtilsCalcul.calculateRectangularBoundsAccordingToCurrentLocation(radius, currentLocation)).thenReturn(list);
    }



}