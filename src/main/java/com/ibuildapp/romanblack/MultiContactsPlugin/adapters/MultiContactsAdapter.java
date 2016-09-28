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
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appbuilder.sdk.android.Utils;
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
    private List<Person> persons = new ArrayList<>();
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
        row = (row == null) ? layoutInflater.inflate(R.layout.grouped_contacts_person_item, null) : row;

        // text
        TextView name = (TextView) row.findViewById(R.id.grouped_contacts_person_text);
        name.setText(persons.get(arg0).getName());
        name.setTextColor(Statics.color3);

        View separator = row.findViewById(R.id.grouped_contacts_person_separator);

        // arrow
        ImageView img = (ImageView) row.findViewById(R.id.grouped_contacts_person_arrow);
        img.setVisibility(View.INVISIBLE);
        if (isDark) {
            separator.setBackgroundColor(Color.parseColor("#4D000000"));
        }  else  {
            separator.setBackgroundColor(Color.parseColor("#4DFFFFFF"));
        }

        if (Utils.isChemeDark(Statics.color3))
            img.setBackgroundResource(R.drawable.gc_members_dark);
        else
            img.setBackgroundResource(R.drawable.gc_members);

        return row;
    }
}
