package com.example.go4lunch.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UtilsCalculTest
{

    private LatLng latLngRight = new LatLng(48.8376158, 2.2303507);
    private LatLng latLngLeft = new LatLng(48.8376158, 2.2303507);
    private LatLng currentLatLng = new LatLng(48.8376158, 2.2303507);

    private static Location currentLocation = new Location("fusedProvider");
    @BeforeClass
    public static void setUp()
    {
        currentLocation.setLatitude(48.8376158);
        currentLocation.setLongitude(2.2303507);
    }

    @Test
    public void calculateRadiusSinceCurrentLocation_0_Success()
    {
        double toCompare = UtilsCalcul.calculateRadiusAccordingToCurrentLocation(latLngRight, latLngLeft, currentLatLng);
        assertEquals(toCompare, 0.0,0);
    }

    @Test
    public void calculateRectangularBoundsSinceCurrentLocation_0_Success()
    {
        List <LatLng> listTest = new ArrayList<>();
        listTest.add(latLngRight);
        listTest.add(latLngLeft);

        List<LatLng> forTest = UtilsCalcul.calculateRectangularBoundsAccordingToCurrentLocation(0, currentLatLng);
        assertEquals(listTest.get(0), forTest.get(0));
        assertEquals(listTest.get(1), forTest.get(1));
    }



}