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
 * Entity class that represents contact data.
 */
public class Contact implements Serializable {

    /**
     * Represents contact type.
     * 0 - name.
     * 1 - phone.
     * 2 - email.
     * 3 - URL.
     * 4 - location.
     * 5 - avatar.
     * 6 - group name.
     */
    private int _type;
    private String _title = "";
    private String _description = "";
    private int _latitude;
    private int _longitude;
    private String avatarUrl = "";

    /**
     * Constructs new contact with given type.
     * @param type the contact type
     */
    public Contact(int type) {
        _type = type;

        if (_type == 4) {
            _title = "";
            _description = "";
            _latitude = 0;
            _longitude = 0;
        } else if ((_type >= 0) || (_type <= 3)) {
            _title = "";
            _description = "";
        }
    }

    /**
     * Sets the contact data.
     * @param title the contact title
     * @param description the contact description
     * @param latitude the contact latitude (location type contacts only)
     * @param longitude the contact longitude (location type contacts only)
     */
    public void setContact(String title, String description, int latitude, int longitude) {
        setTitle(title);
        setDescription(description);

        if (_type == 4) {
            setLatitude(latitude);
            setLongitude(longitude);
        }
    }

    /**
     * Returns the contact type.
     * @return the contact type
     */
    public int getType() {
        return _type;
    }

    /**
     * Returns the contact description.
     * @return the contact description
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Returns the contact title.
     * @return the contact title
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Returns the contact longitude (location type contacts only).
     * @return the contact longitude
     */
    public int getLongitude() {
        if (_type == 4) {
            return _longitude;
        } else {
            return 0;
        }
    }

    /**
     * Retutrns the contact latitude (location type contacts only).
     * @return the contact longitude
     */
    public int getLatitude() {
        if (_type == 4) {
            return _latitude;
        } else {
            return 0;
        }
    }

    /**
     * Sets the contact title.
     * @param title the title to set
     */
    public void setTitle(String title) {
        this._title = title;
    }

    /**
     * Sets the contact description.
     * @param description the description to set
     */
    public void setDescription(String description) {
        this._description = description;
    }

    /**
     * Sets the contact latitude (location type contacts only).
     * @param latitude the latitude to set
     */
    public void setLatitude(int latitude) {
        this._latitude = latitude;
    }

    /**
     * Sets the contact longitude (location type contacts only).
     * @param longitude the longitude to set
     */
    public void setLongitude(int longitude) {
        this._longitude = longitude;
    }

    /**
     * Returns the contact avatar URL (avatar type contacts only).
     * @return the avatar URL
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Sets the contact avatar URL (avatar type contacts only).
     * @param avatarUrl the avatar URL to set
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
