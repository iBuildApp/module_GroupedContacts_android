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
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibuildapp.romanblack.MultiContactsPlugin.R;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.PluginData;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.Statics;

import java.util.HashMap;
import java.util.List;

/**
 * This adapter provides group views for MultiContactsPlugin.
 */
public class GroupContactsAdapter extends BaseAdapter {

    private final boolean isDark;
    List<String> cats;
    private LayoutInflater inflater;
    private HashMap<String, Integer> counts;

    /**
     * Constructs new GroupContactsAdapter with given params.
     * @param context activity that using this adapter
     * @param cats contact categories list
     * @param isDark flag that shows if color cheme is dark
     */
    public GroupContactsAdapter(Context context, List<String> cats, boolean isDark) {
        this.cats = cats;
        this.isDark = isDark;
        inflater = LayoutInflater.from(context);
        counts = PluginData.getInstance().getCategoriesCount();
    }

    public int getCount() {
        if (cats != null) {
            return cats.size();
        } else {
            return 0;
        }
    }

    public Object getItem(int arg0) {
        if (cats != null) {
            return cats.get(arg0);
        } else {
            return null;
        }
    }

    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View row = convertView;
        row = row == null ? inflater.inflate(R.layout.grouped_contacts_person_item, null) : row;

        // text
        TextView name = (TextView) row.findViewById(R.id.grouped_contacts_person_text);
        TextView personCount = (TextView) row.findViewById(R.id.grouped_contacts_person_count);
        View separator = row.findViewById(R.id.grouped_contacts_person_separator);

        name.setText(cats.get(position));
        String itemCount = counts.containsKey(cats.get(position))?String.valueOf(counts.get(cats.get(position))):"";
        personCount.setText(itemCount);

        name.setTextColor(Statics.color3);
        personCount.setTextColor(Statics.color3);

        ImageView img = (ImageView) row.findViewById(R.id.grouped_contacts_person_arrow);
        if (isDark) {
            separator.setBackgroundColor(Color.parseColor("#4D000000"));
        }  else  {
            separator.setBackgroundColor(Color.parseColor("#4DFFFFFF"));
        }

        img.setVisibility(View.VISIBLE);
        img.setBackgroundResource(R.drawable.gc_members);
        img.getBackground().setColorFilter(Statics.color3, PorterDuff.Mode.MULTIPLY);
        return row;
    }
}
