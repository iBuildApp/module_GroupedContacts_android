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
import com.ibuildapp.romanblack.MultiContactsPlugin.R;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.Statics;

import java.util.List;

/**
 * This adapter provides group views for MultiContactsPlugin.
 */
public class GroupContactsAdapter extends BaseAdapter {

    private final boolean isDark;
    List<String> cats;
    private LayoutInflater inflater;

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
        row = row == null ? inflater.inflate(R.layout.romanblack_multicontacts_person_item, null) : row;

        // text
        TextView name = (TextView) row.findViewById(R.id.romanblack_multicontacts_person_item);
        name.setText(cats.get(position));
        name.setTextColor(Statics.color3);
        if ((position > 0) && (position < (cats.size() - 1))) {
            name.setPadding(5, 4, 0, 4);
        }

        // arrow
        ImageView img = (ImageView) row.findViewById(R.id.romanblack_multicontacts_details_arrow);
        img.setImageResource(R.drawable.romanblack_multicontacts_arrow_light);

        // background
        if (cats.size() == 1) {
            if (isDark) {
                row.setBackgroundResource(R.drawable.romanblack_multicontacts_rowsingle_light);
            } else {
                row.setBackgroundResource(R.drawable.romanblack_multicontacts_rowsingle_dark);
            }

        } else {
            if (position == 0) {
                if (isDark) {
                    row.setBackgroundResource(R.drawable.romanblack_multicontacts_rowfirst_light);
                } else {
                    row.setBackgroundResource(R.drawable.romanblack_multicontacts_rowfirst_dark);
                }
            } else if (position == cats.size() - 1) {
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
