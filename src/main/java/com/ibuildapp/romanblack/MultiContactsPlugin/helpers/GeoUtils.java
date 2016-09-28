package com.ibuildapp.romanblack.MultiContactsPlugin.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public abstract class GeoUtils {
    private static final String LOG_NAME = "GEO";

    public static Address getAddressByName(Context context, String addressString) throws Exception {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(addressString, 1);
            if (addresses.size() == 0)
                throw new Exception("Address not found");
            else return addresses.get(0);
        } catch (IOException e) {
            throw e;
        }
    }

    public static class CenterCalculator{
        private LatLng center;
        private int initZoom;

        public void init(Set<LatLng> markers){
            double maxLat = -90;
            double maxLng = -180;
            double minLat = 90;
            double minLng = 180;

            for (LatLng marker : markers){
                if (marker.latitude > maxLat)
                    maxLat = marker.latitude;

                if (marker.longitude > maxLng)
                    maxLng = marker.longitude;

                if (marker.latitude < minLat)
                    minLat = marker.latitude;

                if (marker.longitude < minLng)
                    minLng = marker.longitude;
            }
            center =  new LatLng((maxLat + minLat) / 2, (maxLng + minLng) / 2);

            double gr = Math.abs(maxLng - minLng);
            int z = 1;
            if (gr > (120)) {
                z = 1;
            } else if (gr > (60)) {
                z = 2;
            } else if (gr > (30)) {
                z = 3;
            } else if (gr > (15)) {
                z = 4;
            } else if (gr > (8)) {
                z = 5;
            } else if (gr > (4)) {
                z = 6;
            } else if (gr > (2)) {
                z = 7;
            } else if (gr > (1)) {
                z = 8;
            } else if (gr > (0.5)) {
                z = 9;
            } else if(gr > (0.3)){
                z = 10;
            }else if(gr > (0.1)){
                z = 11;
            }else z = 12;

            initZoom = 2*(z/3);
        }

        public LatLng getCenter() {
            return center;
        }

        public int getInitZoom() {
            return initZoom;
        }
    }
}