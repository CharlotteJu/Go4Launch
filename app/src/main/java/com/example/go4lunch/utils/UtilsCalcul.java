package com.example.go4lunch.utils;

import android.location.Location;

import com.example.go4lunch.model.RestaurantPOJO;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public abstract class UtilsCalcul
{
    public static double calculateRadiusSinceCurrentLocation(LatLng latLngRight, LatLng latLngLeft, Location currentLocation)
    {
        // L'objectif est de calculer la distance entre la position actuelle et les coins haut-gauche et haut-droit de l'ecran

        // ------------------------------------------------------------------------------------------------------------------
        // on se sert des formules suivantes :
        // convertion degree vers radian : 360 degrees = 2 PI radian => 1 degree = 2 PI / 360.
        final float DEG_EN_RADIAN = 2.0f * (float)Math.PI / 360.0f;

        // 1 degree en latitude = 111,32 km (111320 m)
        final float OUVERTURE_LAT_EN_METRES = 111320.0f;

        // 1 degree en longitude = 111,32 km * cos(latitude)    /!\ dans un fonction sinus, cosinu ou tangeante, la latitude doit etre exprimée en radian (et non pas en degree)
        // ici on utilise la latitude de la position courante car les points sont très - TRES - proches les uns des autres (c'est une approximation acceptable)
        final float OUVERTURE_LONG_EN_METRES = 111320.0f * (float)Math.cos(currentLocation.getLatitude() * DEG_EN_RADIAN);

        // theoreme de pythagore pour un triangle rectangle : c (hypothenuse) = √(a² + b²)

        // ------------------------------------------------------------------------------------------------------------------
        // Distance position actuelle <=> Coin haut-gauche
        float a_gauche, b_gauche;
        // ouverture de la latitude en degree ET convertion de l'ouverture en metres
        a_gauche = Math.abs((float)(latLngLeft.latitude - currentLocation.getLatitude())) * OUVERTURE_LAT_EN_METRES;

        // meme chose en longitude
        b_gauche = Math.abs((float)(latLngLeft.longitude - currentLocation.getLongitude())) * OUVERTURE_LONG_EN_METRES;

        // Math.sqrt() = fonction racine carree
        float dist_PositionCourante_CoinHautGauche = (float)Math.sqrt((a_gauche * a_gauche) + (b_gauche * b_gauche));

        // ------------------------------------------------------------------------------------------------------------------
        // Distance position actuelle <=> Coin haut-droit
        float a_droit, b_droit;
        a_droit = Math.abs((float)(latLngRight.latitude - currentLocation.getLatitude())) * OUVERTURE_LAT_EN_METRES;
        b_droit = Math.abs((float)(latLngRight.longitude - currentLocation.getLongitude())) * OUVERTURE_LONG_EN_METRES;
        float dist_PositionCourante_CoinHautDroit = (float)Math.sqrt((a_droit * a_droit) + (b_droit * b_droit));

        if (dist_PositionCourante_CoinHautDroit > 10000 || dist_PositionCourante_CoinHautGauche > 10000)
        {
            return 10000;
        }
        else
            return Math.max(dist_PositionCourante_CoinHautDroit, dist_PositionCourante_CoinHautGauche);

    }

    public static List<LatLng> calculateRectangularBoundsSinceCurrentLocation(double radius, Location currentLocation)
    {
        List<LatLng> list = new ArrayList<>();

        double latA = currentLocation.getLatitude() - (radius/111);
        double lngA =  currentLocation.getLongitude() - (radius/(111 * Math.cos(latA * (Math.PI/180.0f)))) ;
        LatLng pointA = new LatLng(latA, lngA);
        list.add(pointA);


        double latB = currentLocation.getLatitude() + radius/111 ;
        double lngB = currentLocation.getLongitude() + radius/(111 * Math.cos(latB * (Math.PI/180.0f)));

        LatLng pointB = new LatLng(latB, lngB);
        list.add(pointB);

        return list;
    }
}
