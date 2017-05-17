package com.ibuildapp.romanblack.MultiContactsPlugin.maps;


import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface GoogleMapsApi {
    @GET("/maps/api/geocode/json")
    Observable<MapResponse> getAddressLocation(@Query("address") String address, @Query("sensor") String sensor);
}
