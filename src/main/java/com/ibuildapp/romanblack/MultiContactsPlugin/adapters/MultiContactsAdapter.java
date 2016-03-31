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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.Person;
import com.ibuildapp.romanblack.MultiContactsPlugin.R;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.Statics;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter provides person views for MultiContactsActivity.
 */
public class MultiContactsAdapter extends BaseAdapter {

    private Context ctx = null;
    private LayoutInflater layoutInflater = null;
    private List<Person> persons = new ArrayList<Person>();
    private boolean isDark;

    /**
     * Constructs new MultiContactsAdapter with given params.
     * @param ctx activity that using this adapter
     * @param persons persons list
     * @param isDark flag that shows if color cheme is dark
     */
    public MultiContactsAdapter(Context ctx, List<Person> persons, boolean isDark) {
        this.persons = persons;
        this.ctx = ctx;
        layoutInflater = LayoutInflater.from(this.ctx);
        this.isDark = isDark;
    }

    public int getCount() {
        if (persons != null) {
            return persons.size();
        } else {
            return 0;
        }
    }

    public Object getItem(int arg0) {
        if (persons != null) {
            return persons.get(arg0);
        } else {
            return null;
        }
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int arg0, View arg1, ViewGroup arg2) {
        View row = arg1;
        row = (row == null) ? layoutInflater.inflate(R.layout.romanblack_multicontacts_person_item, null) : row;

        // text
        TextView name = (TextView) row.findViewById(R.id.romanblack_multicontacts_person_item);
        name.setText(persons.get(arg0).getName());
        name.setTextColor(Statics.color3);
        if ((arg0 > 0) && (arg0 < (persons.size() - 1))) {
            name.setPadding(5, 4, 0, 4);
        }

        // arrow
        ImageView img = (ImageView) row.findViewById(R.id.romanblack_multicontacts_details_arrow);
        img.setImageResource(R.drawable.romanblack_multicontacts_arrow_light);

        // background
        if (persons.size() == 1) {
            if (isDark) {
                row.setBackgroundResource(R.drawable.romanblack_multicontacts_rowsingle_light);
            } else {
                row.setBackgroundResource(R.drawable.romanblack_multicontacts_rowsingle_dark);
            }
        } else {
            if (arg0 == 0) {
                if (isDark) {
                    row.setBackgroundResource(R.drawable.romanblack_multicontacts_rowfirst_light);
                } else {
                    row.setBackgroundResource(R.drawable.romanblack_multicontacts_rowfirst_dark);
                }
            } else if (arg0 == persons.size() - 1) {
                if (isDark) {
                    row.setBackgroundResource(R.drawable.romanblack_multicontacts_rowlast_light);
                } else {
                    row.setBackgroundResource(R.drawable.romanblack_multicontacts_rowlast_dark);
                }
            } else {
                if (isDark) {
                    row.setBackgroundResource(R.drawable.romanblack_multicontacts_rowmiddle_light);
                } else {
                    row.setBackgroundResource(R.drawable.romanblack_multicontacts_rowmiddle_dark);
                }
            }
        }

        return row;
    }
}
