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

import android.graphics.Color;
import android.util.Log;
import android.util.Xml;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.Contact;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.Person;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * This class using for parsing module xml data.
 */
public class EntityParser {

    private int color1 = Color.parseColor("#4d4948"); //BackGround
    private int color2 = Color.parseColor("#fff58d");
    private int color3 = Color.parseColor("#fff7a2");
    private int color4 = Color.parseColor("#ffffff");
    private int color5 = Color.parseColor("#bbbbbb");
    private boolean hasColorSchema = true;
    private String xmlData = null;
    private ArrayList<Contact> contacts = null;
    private ArrayList<Person> persons = null;

    /**
     * Constructs new EntityParser instance.
     */
    public EntityParser() {
        this.xmlData = "";
        this.contacts = new ArrayList<Contact>();
    }

    /**
     * Constructs new EntityParser instance.
     * @param xml - module xml data to parse
     */
    public EntityParser(String xmlData) {
        this.xmlData = xmlData;
        this.contacts = new ArrayList<Contact>();
    }

    /**
     * Returns parsed persons list.
     * @return the persons list
     */
    public ArrayList<Person> getPersons() {
        return persons;
    }

    /**
     * @return parsed color 1 of color scheme
     */
    public int getColor1() {
        return color1;
    }

    /**
     * @return parsed color 2 of color scheme
     */
    public int getColor2() {
        return color2;
    }

    /**
     * @return parsed color 3 of color scheme
     */
    public int getColor3() {
        return color3;
    }

    /**
     * @return parsed color 4 of color scheme
     */
    public int getColor4() {
        return color4;
    }

    /**
     * @return parsed color 5 of color scheme
     */
    public int getColor5() {
        return color5;
    }

    /**
     * Sax handler that handle configuration xml tags and prepare module data structure.
     */
    private class SaxHandler extends DefaultHandler {

        private boolean contactBegined = false;
        private Contact currentContact = null;
        private Person currentPerson = null;
        private StringBuilder sb = new StringBuilder();

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();

            EntityParser.this.persons = new ArrayList<Person>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            if (localName.equalsIgnoreCase("person")) {
                currentPerson = new Person();
            } else if (localName.equalsIgnoreCase("con")) {
                contactBegined = true;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);

            sb.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            if (localName.equalsIgnoreCase("color1")) {
                try {
                    color1 = Color.parseColor(sb.toString().trim());
                    hasColorSchema = true;
                } catch (Exception ex) {
                }
            }

            if (localName.equalsIgnoreCase("color2")) {
                try {
                    color2 = Color.parseColor(sb.toString().trim());
                } catch (Exception ex) {
                }
            }

            if (localName.equalsIgnoreCase("color3")) {
                try {
                    color3 = Color.parseColor(sb.toString().trim());
                } catch (Exception ex) {
                }
            }

            if (localName.equalsIgnoreCase("color4")) {
                try {
                    color4 = Color.parseColor(sb.toString().trim());
                } catch (Exception ex) {
                }
            }

            if (localName.equalsIgnoreCase("color5")) {
                try {
                    color5 = Color.parseColor(sb.toString().trim());
                } catch (Exception ex) {
                }
            }

            if (localName.equalsIgnoreCase("person")) {
                persons.add(currentPerson);
                currentPerson = null;
            }

            if (localName.equalsIgnoreCase("con")) {
                currentPerson.addContact(currentContact);
                contactBegined = false;
                currentContact = null;
            }

            if (currentPerson != null) {
                if (localName.equalsIgnoreCase("type") && contactBegined) {
                    currentContact = new Contact(Integer.parseInt(sb.toString().trim()));
                }

                if (currentContact != null) {
                    if (localName.equalsIgnoreCase("title")) {
                        currentContact.setTitle(sb.toString().trim());
                    } else if (localName.equalsIgnoreCase("description")) {
                        currentContact.setDescription(sb.toString().trim());
                    }
                }
            }

            sb.setLength(0);
        }
    };

    /**
     * Parses module data that was set in constructor.
     * @return the parsed persons list
     */
    public ArrayList<Person> parse() {
        if (this.xmlData == null) {
            return null;
        }

        if (this.xmlData.length() == 0) {
            return null;
        }

        return parse(this.xmlData);
    }

    /**
     * Parses module data.
     * @param xmlData the module data to parse
     * @return the parsed persons list
     */
    public ArrayList<Person> parse(String xmlData) {
        try {
            Xml.parse(xmlData, new SaxHandler());
        } catch (SAXException sAXEx) {
            Log.e("", "");
        }

        return persons;
    }

    /**
     * Sets the module XML data to parse
     * @param xmlData the XML Data to set
     */
    public void setXmlData(String xmlData) {
        this.xmlData = xmlData;
    }

    /**
     * Checks if this module has color scheme.
     * @return true if this module has color sheme
     */
    public boolean isHasColorSchema() {
        return hasColorSchema;
    }
}
