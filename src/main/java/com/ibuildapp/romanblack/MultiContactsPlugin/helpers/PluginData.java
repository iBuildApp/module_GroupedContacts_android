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
package com.ibuildapp.romanblack.MultiContactsPlugin.helpers;

import android.util.Log;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.Person;

import java.util.*;

/**
 * This class using to store static module data.
 */
public class PluginData {

    private static PluginData ourInstance = new PluginData();
    private List<Person> persons;
    private List<String> categories;
    private HashMap<String, Integer> categoriesCount;
    private boolean hasColorSchema;

    /**
     * Returns new PluginData instance.
     * @return new PluginData instance
     */
    public static PluginData getInstance()  {
        return ourInstance;
    }

    /**
     * Constructs new PluginData instance.
     */
    private PluginData() {
    }

    /**
     * Sets the list of contact persons.
     * @param persons the list of persons
     */
    public void setPersons(List<Person> persons) {
        this.persons = persons;
        categories = new ArrayList<String>();

        // вытаскиваем все категории
        for (Person p : persons) {
            String c = p.getCategory();
            if (c != null && !categories.contains(c)) {
                categories.add(c);
            }
        }

        Log.d("", "");
    }

    /**
     * Returns the list of unique person categories.
     * @return the list of categories
     */
    public List<String> getCategories() {
        List<String> cats = new ArrayList<String>();
        cats.addAll(categories);
        //Collections.sort(cats);
        return cats;
    }

    /**
     * Search persons those name contains the search string.
     * @param key the search string
     * @return the list of found persons
     */ 
    public List<Person> searchByString(String key) {
        Set<Person> personSet = new HashSet<Person>();
        for (Person p : persons) {
            if (p.getName() != null && p.getName().toLowerCase().contains(key.toLowerCase())) {
                personSet.add(p);
            }
            if (p.getAddress() != null && p.getAddress().toLowerCase().contains(key.toLowerCase())) {
                personSet.add(p);
            }
            if (p.getPhone() != null && p.getPhone().toLowerCase().contains(key.toLowerCase())) {
                personSet.add(p);
            }
        }

        List<Person> personList = new ArrayList<Person>();
        personList.addAll(personSet);
        return personList;
    }

    /**
     * Checks if this module has color scheme.
     * @return true if this module has color scheme 
     */
    public boolean isHasColorSchema() {
        return hasColorSchema;
    }

    /**
     * Sets the color scheme flag.
     * @param hasColorSchema the flag to set
     */
    public void setHasColorSchema(boolean hasColorSchema) {
        this.hasColorSchema = hasColorSchema;
    }

    /**
     * Returns the list of all module persons.
     * @return the list of persons
     */
    public List<Person> getPersons() {
        return persons;
    }



    public void prepareCounts() {
        for (String category :categories) {
            int count = 0;
            for (Person p : persons) {

                String c = p.getCategory();
                if (c != null && c.equals(category)) {
                    count++;
                }
            }

            if (categoriesCount == null)
                categoriesCount = new HashMap<>();

            categoriesCount.put(category, count);
        }
    }
    public HashMap<String, Integer> getCategoriesCount() {
        return categoriesCount;
    }

    public void setCategoriesCount(HashMap<String, Integer> categoriesCount) {
        this.categoriesCount = categoriesCount;
    }

}
