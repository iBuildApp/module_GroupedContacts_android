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
package com.ibuildapp.romanblack.MultiContactsPlugin.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.Contact;
import com.ibuildapp.romanblack.MultiContactsPlugin.R;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.Statics;

import java.util.ArrayList;

/**
 * This adapter provides contact views for ContactDetailsActivity.
 */
public class ContactDetailsAdapter extends ArrayAdapter<Contact> {

    public LayoutInflater inflater;
    public ArrayList<Contact> contacts;
    private boolean isDark;
    private ArrayList<Integer> blockPositoins = new ArrayList<Integer>();

    /**
     * Constructs new ContactDetailsAdapter with given params.
     * @param context activity that using this adapter
     * @param resource layout resource ID
     * @param objects contacts list
     * @param isDark flag that shows if color cheme is dark
     */
    public ContactDetailsAdapter(Activity context, int resource, ArrayList<Contact> objects, boolean isDark) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        contacts = objects;
        this.isDark = isDark;

        // remove category
        for (Contact c : objects) {
            if (c.getTitle().equals("category")) {
                objects.remove(c);
                break;
            }
        }

        // identify contact with type name
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).getTitle().compareTo("name") == 0) {
                blockPositoins.add(i);
            }
        }
    }

    /**
     * This class is using for better ListView perfomance. 
     */
    static class ViewHolder {

        public TextView title;
        public TextView description;
        public ImageView img;
        public View separator;
    }

    @Override
    public boolean isEnabled(int position) {

        for (Integer s : blockPositoins) {
            if (position == s) {
                return false;
            }
        }

        return true;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        holder = new ViewHolder();

        // UI links
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.romanblack_multicontacts_details_item, null, true);
            holder.title = (TextView) convertView.findViewById(R.id.romanblack_multicontacts_details_title);
            holder.description = (TextView) convertView.findViewById(R.id.romanblack_multicontacts_details_description);
            holder.img = (ImageView) convertView.findViewById(R.id.romanblack_multicontacts_details_imgview);
            holder.separator = convertView.findViewById(R.id.gc_item_separator);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (contacts.get(position).getType()) {
            case 0:
                holder.img.setImageBitmap(Statics.appyColorFilterForResource(getContext(), R.drawable.romanblack_multicontacts_contactico, Statics.color3, PorterDuff.Mode.MULTIPLY));
                holder.description.setText(contacts.get(position).getDescription());
                break;
            case 1:
                holder.img.setImageBitmap(Statics.appyColorFilterForResource(getContext(), R.drawable.gc_call_white, Statics.color3, PorterDuff.Mode.MULTIPLY));

                holder.description.setText(contacts.get(position).getDescription());
                break;
            case 2:
                holder.img.setImageBitmap(Statics.appyColorFilterForResource(getContext(), R.drawable.gc_email_white, Statics.color3, PorterDuff.Mode.MULTIPLY));
                holder.description.setText(contacts.get(position).getDescription());
                break;
            case 3:
                holder.img.setImageBitmap(Statics.appyColorFilterForResource(getContext(), R.drawable.gc_www_white, Statics.color3, PorterDuff.Mode.MULTIPLY));
                holder.description.setText(contacts.get(position).getDescription());
                break;
            case 4:
                holder.img.setImageBitmap(Statics.appyColorFilterForResource(getContext(), R.drawable.gc_location_white, Statics.color3, PorterDuff.Mode.MULTIPLY));
                holder.description.setText(contacts.get(position).getDescription());
                break;
        }
        if (isDark)
            holder.separator.setBackgroundColor(Color.parseColor("#4D000000"));
          else
            holder.separator.setBackgroundColor(Color.parseColor("#4DFFFFFF"));

        if (position == contacts.size() - 1)
            holder.separator.setVisibility(View.INVISIBLE);
        else holder.separator.setVisibility(View.VISIBLE);

        holder.title.setText(contacts.get(position).getTitle());
        holder.title.setTextColor(Statics.color3);
        holder.description.setTextColor(Statics.color3);
        holder.title.setVisibility(View.GONE);
        return convertView;
    }
}
