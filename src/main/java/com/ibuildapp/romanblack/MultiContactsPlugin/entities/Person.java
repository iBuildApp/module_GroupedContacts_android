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
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Entity class that represents contact person.
 */
public class Person implements Serializable {

    private String phone;

    /**
     * Constructs new person.
     */
    public Person() {
        contacts = new ArrayList<Contact>();
    }
    
    private ArrayList<Contact> contacts = null;

    /**
     * Returns the person name depending on contact info.
     * @return the name
     */
    public String getName() {
        for (Iterator<Contact> iterator = contacts.iterator();
                iterator.hasNext();) {
            Contact contact = iterator.next();

            if (contact.getType() == 0) {
                return contact.getDescription();
            }
        }

        return null;
    }

    /**
     * Checks if this person has avatar.
     * @return true if this person has avatar, false otherwise
     */
    public boolean hasAvatar() {
        for (Iterator<Contact> iterator = contacts.iterator();
                iterator.hasNext();) {
            Contact contact = iterator.next();

            if (contact.getType() == 5) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if this person has name.
     * @return true if this person has name
     */
    public boolean hasName() {
        for (Iterator<Contact> iterator = contacts.iterator();
                iterator.hasNext();) {
            Contact contact = iterator.next();

            if ((contact.getType() == 0)
                    && (contact.getDescription().length() != 0)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the person avatar image URL
     * @return the image URL or null if this person does not have avatar
     */
    public String getAvatarUrl() {
        for (Iterator<Contact> iterator = contacts.iterator();
                iterator.hasNext();) {
            Contact contact = iterator.next();

            if (contact.getType() == 5) {
                return contact.getDescription();
            }
        }

        return null;
    }

    /**
     * Adds the contact info.
     * @param contact contact info item
     */
    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    /**
     * Returns the person contact info.
     * @return the list of contact items
     */
    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    /**
     * Sets the contact info.
     * @param contacts the list of contact items
     */
    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    /**
     * Returns the person's category.
     * @return the category
     */
    public String getCategory() {
        for (Contact c : contacts) {
            if (c.getType() == 6) {
                return c.getDescription();
            }
        }

        return null;
    }

    /**
     * Returns the person's phone number.
     * @return the phone number string
     */
    public String getPhone() {

        for (Contact c : contacts) {
            if (c.getType() == 1) {
                return c.getDescription();
            }
        }

        return phone;
    }

    /**
     * Returns the person's email.
     * @return the email string
     */
    public String getEmail() {

        for (Contact c : contacts) {
            if (c.getType() == 2) {
                return c.getDescription();
            }
        }

        return null;
    }

    /**
     * Returns the person's address.
     * @return the address string
     */
    public String getAddress() {
        for (Contact c : contacts) {
            if (c.getType() == 4) {
                return c.getDescription();
            }
        }

        return null;
    }
}
