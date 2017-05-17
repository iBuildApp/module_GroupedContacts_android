package com.ibuildapp.romanblack.MultiContactsPlugin.maps;

import com.google.gson.annotations.SerializedName;

/**
 * Created by web-developer on 03.05.2017.
 */
public class Results {
    @SerializedName("place_id")
    private String placeId;

    @SerializedName("formatted_address")
    private String formattedAddress;

    private String[] types;

    private Geometry geometry;

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }
}
