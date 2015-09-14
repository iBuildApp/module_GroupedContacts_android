/****************************************************************************
 * *
 * Copyright (C) 2014-2015 iBuildApp, Inc. ( http://ibuildapp.com )         *
 * *
 * This file is part of iBuildApp.                                          *
 * *
 * This Source Code Form is subject to the terms of the iBuildApp License.  *
 * You can obtain one at http://ibuildapp.com/license/                      *
 * *
 ****************************************************************************/
package com.ibuildapp.romanblack.MultiContactsPlugin.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ibuildapp.romanblack.MultiContactsPlugin.R;

import java.util.ArrayList;
import java.util.List;

public class CallDialog extends Dialog {

    public interface ActionListener {
        void onCall(DialogInterface dialog);
        void onAddContact(DialogInterface dialog);
        void onCancel(DialogInterface dialog);
    }

    public static class Item {

        public interface OnClickListener {
            void onClick();
        }

        private static final int HEIGHT = 60;
        private static final int ICON_MARGIN_LR = 10;
        private static final int TITLE_TEXT_SIZE = 20;

        private String text;

        private Item(String text) {
            this.text = text;
        }

    }

    private class ListView extends android.widget.ListView {

        ListView(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            listView.setAdapter(adapter);
        }

    }

    private class Adapter extends BaseAdapter {

        List<Item> items;
        Context context;
        float density;
        int iconMarginLR;

        Adapter(Context context, List<Item> items) {
            this.items = items;
            this.context = context;

            density = context.getResources().getDisplayMetrics().density;
            iconMarginLR = Float.valueOf(Item.ICON_MARGIN_LR * density).intValue();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Item getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Item item = getItem(position);

            if(convertView == null)
                convertView = inflateItemView();

            TextView typedConvertView = ((TextView)convertView);
            typedConvertView.setText(item.text);

            return convertView;
        }

        private TextView inflateItemView() {
            return new TextView(context) {{
                setBackgroundColor(context.getResources().getColor(android.R.color.white));
                setTextColor(Color.parseColor("#cc000000"));
                setLayoutParams(new ListView.LayoutParams(listView.getMeasuredWidth(), Float.valueOf(Item.HEIGHT * density).intValue()));
                setTextSize(TypedValue.COMPLEX_UNIT_SP, Item.TITLE_TEXT_SIZE);
                setPadding(iconMarginLR, 0, iconMarginLR, 0);
                setGravity(Gravity.CENTER);
            }};
        }

    }

    Adapter adapter;
    ListView listView;

    public CallDialog(final Context context, final String number, final ActionListener actionListener) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);

        adapter = new Adapter(context, new ArrayList<Item>() {{
            add(0, new Item(number));
            add(1, new Item(context.getResources().getString(R.string.romanblack_multicontacts_call)));
            add(2, new Item(context.getResources().getString(R.string.multicontacts_add_to_phonebook)));
            add(3, new Item(context.getResources().getString(R.string.common_cancel_upper)));
        }});

        listView = new ListView(context);
        listView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        listView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        listView.setDivider(new ColorDrawable(Color.parseColor("#33000000")));
        listView.setDividerHeight(1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1)
                    actionListener.onCall(CallDialog.this);
                else if (position == 2)
                    actionListener.onAddContact(CallDialog.this);
                else if (position == 3)
                    actionListener.onCancel(CallDialog.this);
            }
        });

        setContentView(listView);
    }

}
