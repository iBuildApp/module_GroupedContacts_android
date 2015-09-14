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

import java.io.Serializable;

/**
 * Entity class that represents map point.
 */
public class MapItem implements Serializable {

    /**
     * Constructs new MapItem.
     */
    public MapItem() {
    }
    
    private static final long serialVersionUID = 1L;
    private String title = "";
    private String subtitle = "";
    private String description = "";
    private double longitude = 0;
    private double latitude = 0;
    private String url = "";

    /**
     * Returns map point redirect URL.
     * @return redirect URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the map point redirect URL.
     * @param url the redirect URL to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Sets the map point title.
     * @param value the title to set
     */
    public void setTitle(String value) {
        title = value;
    }

    /**
     * Returns the map point title.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the map point subtitle.
     * @param value the subtitle to set
     */
    public void setSubtitle(String value) {
        subtitle = value;
    }

    /**
     * Returns the map point subtitle.
     * @return the subtitle
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Sets the map point description.
     * @param value the description to set
     */
    public void setDescription(String value) {
        description = value;
    }

    /**
     * Retutrns the map point description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the map point longitude.
     * @param value the longitude to set
     */
    public void setLongitude(double value) {
        longitude = value;
    }

    /**
     * Returns the map point longitude.
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the map point latitude.
     * @param value the latitude to set
     */
    public void setLatitude(double value) {
        latitude = value;
    }

    /**
     * Retutrns the map point latitude.
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }
}
