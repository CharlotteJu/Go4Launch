package com.example.go4lunch.utils;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public abstract class UtilsCalcul
{

    /**
     * Calculate the Radius from the CurrentLocation taking into account the VisibleRegion of Map
     * @param latLngRight from the VisibleRegion of Map
     * @param latLngLeft from the VisibleRegion of Map
     * @param currentLocation of the currentUser
     * @return radius
     */
    public static int calculateRadiusAccordingToCurrentLocation(LatLng latLngRight, LatLng latLngLeft, LatLng currentLocation)
    {
        // Conversion degree into radian : 360 degrees = 2 PI radian => 1 degree = 2 PI / 360.
        final float DEG_IN_RADIAN = 2.0f * (float)Math.PI / 360.0f;

        // 1 degree in latitude = 111,32 km (111320 m)
        final float OPENING_LAT_METERS = 111320.0f;

        // 1 degree in longitude = 111,32 km * cos(latitude)    /!\ in a function sinus, cosine or tangent, the latitude has to be in radian (not degree)
        // Here we use the latitude of the currentLocation because the points are next to each other (approximation)
        final float OPENING_LNG_METERS = 111320.0f * (float)Math.cos(currentLocation.latitude * DEG_IN_RADIAN);

        // Pythagore for a right triangle : c (hypotenuse) = √(a² + b²)
        // ------------------------------------------------------------------------------------------------------------------
        // Distance currentLocation <=> latLngLeft
        float a_left;
        float b_left;
        // Opening of the latitude in degree and conversion at the opening in meters
        a_left = Math.abs((float)(latLngLeft.latitude - currentLocation.latitude)) * OPENING_LAT_METERS;

        // Same in longitude
        b_left = Math.abs((float)(latLngLeft.longitude - currentLocation.longitude)) * OPENING_LNG_METERS;

        // Math.sqrt() = function square root
        float dist_currentLocation_latLngLeft = (float)Math.sqrt((a_left * a_left) + (b_left * b_left));

        // ------------------------------------------------------------------------------------------------------------------
        //  Distance currentLocation <=> latLngRight
        float a_right;
        float b_right;
        a_right = Math.abs((float)(latLngRight.latitude - currentLocation.latitude)) * OPENING_LAT_METERS;
        b_right = Math.abs((float)(latLngRight.longitude - currentLocation.longitude)) * OPENING_LNG_METERS;
        float dist_currentLocation_latLngRight = (float)Math.sqrt((a_right * a_right) + (b_right * b_right));

        // If the distance > 10000, return 10000
        if (dist_currentLocation_latLngRight > 10000 || dist_currentLocation_latLngLeft > 10000)
        {
            return 10000;
        }
        else
        {
            return (int) Math.max(dist_currentLocation_latLngRight, dist_currentLocation_latLngLeft);
        }
    }

    /**
     * Calculate the RectangularBounds for Autocomplete Search from the CurrentLocation according to the radius
     * @param radius from the PlacesRequest
     * @param currentLocation of the CurrentUser
     * @return a List with 2 LatLng
     */
    public static List<LatLng> calculateRectangularBoundsAccordingToCurrentLocation(double radius, LatLng currentLocation)
    {
        List<LatLng> list = new ArrayList<>();

        double latA = currentLocation.latitude - (float) (radius/111);
        double lngA = currentLocation.longitude - (float) (radius/(111 * Math.cos(latA * (Math.PI/180.0f))));
        LatLng pointA = new LatLng(latA, lngA);
        list.add(pointA);

        double latB = currentLocation.latitude + (float) (radius/111) ;
        double lngB = currentLocation.longitude + (float) (radius/(111 * Math.cos(latB * (Math.PI/180.0f))));
        LatLng pointB = new LatLng(latB, lngB);
        list.add(pointB);

        return list;
    }

}
