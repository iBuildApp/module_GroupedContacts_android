/****************************************************************************
*                                                                           *
*  Copyright (C) 2014-2015 iBuildApp, Inc. ( http://ibuildapp.com )         *
*                                                                           *
*  This file is part of iBuildApp.                                          *
*                                                                           *
*  This Source Code Form is subject to the terms of the iBuildApp License.  *
*  You can obtain one at http://ibuildapp.com/license/                      *
*                                                                           *
****************************************************************************/
package com.ibuildapp.romanblack.MultiContactsPlugin.entities;

/**
 * Entity class that represents map location.
 */
public class MapLocation {

    private String title = "";
    private String subtitle = "";
    private String description = "";
    private int latitude = 0;
    private int longitude = 0;

    /**
     * Constructs new map location with given latitude and longitude.
     * @param latitude location latitude
     * @param longitude location longitude
     */
    public MapLocation(double latitude, double longitude) {
        this.latitude = (int) (latitude * 1e6);
        this.longitude = (int) (longitude * 1e6);
    }

    /**
     * Sets the map location title.
     * @param value the title to set
     */
    public void setTitle(String value) {
        title = value;
    }

    /**
     * Returns the map location title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the map location subtitle.
     * @param value the subtitle to set
     */
    public void setSubtitle(String value) {
        subtitle = value;
    }

    /**
     * Returns the map location subtitle.
     * @return the subtitle
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Sets the map location description.
     * @param value the description to set
     */
    public void setDescription(String value) {
        description = value;
    }

    /**
     * Returns the map location description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the map location latitude.
     * @return the latitude
     */
    public int getLatitude() {
        return latitude;
    }

    /**
     * Returns thr map location longitude.
     * @return the longitude
     */
    public int getLongitude() {
        return longitude;
    }
}
