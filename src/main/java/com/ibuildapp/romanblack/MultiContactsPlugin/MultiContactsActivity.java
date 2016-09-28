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
package com.ibuildapp.romanblack.MultiContactsPlugin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.appbuilder.sdk.android.AppBuilderModuleMain;
import com.appbuilder.sdk.android.Utils;
import com.appbuilder.sdk.android.Widget;
import com.ibuildapp.romanblack.MultiContactsPlugin.adapters.MultiContactsAdapter;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.Person;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.PluginData;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.Statics;
import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This activity represents persons list page.
 */
public class MultiContactsActivity extends AppBuilderModuleMain {

    private final int INITIALIZATION_FAILED = 0;
    private final int LOADING_ABORTED = 1;
    private final int SHOW_PERSONS = 2;
    private final int HIDE_PROGRESS_DIALOG = 3;
    private final int THERE_IS_NO_PERSONS = 5;
    private final int SET_ROOT_BACKGROUND = 6;
    private final int RESET_BACKGROUND = 7;
    private final int ONLY_ONE_CONTACT = 1000;
    private boolean backgroundLoadede = false;
    private String cachePath = "";
    private String cacheBackgroundFile = "";
    private ListView listView = null;
    private ProgressDialog progressDialog = null;
    private LinearLayout root = null;
    private Widget widget = null;
    private List<Person> persons = null;
    private List<Person> neededPersons = null;
    private Resources resources;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case INITIALIZATION_FAILED: {
                    Toast.makeText(MultiContactsActivity.this,
                            R.string.alert_cannot_init,
                            Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 5000);
                }
                break;
                case LOADING_ABORTED: {
                    closeActivity();
                }
                break;
                case SHOW_PERSONS: {
                    showPersons();
                }
                break;
                case HIDE_PROGRESS_DIALOG: {
                    hideProgressDialog();
                }
                break;

                case RESET_BACKGROUND: {
                    View v = (View) msg.obj;
                    v.setBackgroundColor(Color.TRANSPARENT);
                }
                break;

                case SET_ROOT_BACKGROUND: {
                    if (PluginData.getInstance().isHasColorSchema()) {
                        root.setBackgroundColor(Statics.color1);
                        backgroundLoadede = true;
                    } else {
                        if (widget.isBackgroundColor()) {
                            if (widget.getBackgroundColor() != Color.TRANSPARENT) {
                                root.setBackgroundColor(widget.getBackgroundColor());
                                backgroundLoadede = true;
                            }
                        } else if (widget.isBackgroundURL()) {
                            try {
                                cacheBackgroundFile = cachePath + "/" + Utils.md5(widget.getBackgroundURL());
                                File backgroundFile = new File(cacheBackgroundFile);
                                if (backgroundFile.exists()) {
                                    root.setBackgroundDrawable(new BitmapDrawable(
                                            BitmapFactory.decodeStream(new FileInputStream(backgroundFile))));
                                    backgroundLoadede = true;
                                } else {
                                    BackgroundDownloadTask dt = new BackgroundDownloadTask();
                                    dt.execute(widget.getBackgroundURL());
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else if (widget.isBackgroundInAssets()) {
                            try {
                                AssetManager am = resources.getAssets();
                                root.setBackgroundDrawable(new BitmapDrawable(
                                        am.open(widget.getBackgroundURL())));
                                backgroundLoadede = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (backgroundLoadede) {
                        handler.sendEmptyMessage(SHOW_PERSONS);
                    }
                }
                break;

                case THERE_IS_NO_PERSONS: {
                    Toast.makeText(MultiContactsActivity.this,
                            R.string.romanblack_multicontacts_alert_no_persons,
                            Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                    finish();
                }
                break;
            }
        }
    };
    private String category;
    private View separator;
    private View backSeparator;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void create() {
        try {
            setContentView(R.layout.grouped_contacts_main);

            setTopBarLeftButtonTextAndColor(getResources().getString(R.string.common_back_upper), getResources().getColor(android.R.color.black), true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    return;
                }
            });
            setTopBarTitleColor(getResources().getColor(android.R.color.black));
            setTopBarBackgroundColor(Statics.color1);

            LinearLayout inputSearchLayout = (LinearLayout) findViewById(R.id.grouped_contacts_search_layout);
            inputSearchLayout.setVisibility(View.GONE);

            resources = getResources();

            Intent currentIntent = getIntent();
            Bundle store = currentIntent.getExtras();
            widget = (Widget) store.getSerializable("Widget");
            category = store.getString("category");
            setTopBarTitle(category);

            if (widget == null) {
                handler.sendEmptyMessage(INITIALIZATION_FAILED);
                return;
            }

            cachePath = widget.getCachePath() + "/contacts-" + widget.getOrder();

            root = (LinearLayout) findViewById(R.id.grouped_contacts_main_root);
            listView = (ListView) findViewById(R.id.grouped_contacts_list);
            separator = findViewById(R.id.gc_head_separator);
            backSeparator = findViewById(R.id.gc_back_separator);

            backSeparator.setBackgroundColor(Statics.color1);
            if (Statics.isLight) {
                separator.setBackgroundColor(Color.parseColor("#4d000000"));
            } else {
                separator.setBackgroundColor(Color.parseColor("#4dFFFFFF"));
            }

            handler.sendEmptyMessage(SET_ROOT_BACKGROUND);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ONLY_ONE_CONTACT: {
                finish();
            }
            break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Shows persons list.
     */
    private void showPersons() {
        try {
            persons = PluginData.getInstance().getPersons();
            neededPersons = new ArrayList<>();
            for (Iterator<Person> it = persons.iterator(); it.hasNext();) {
                Person per = it.next();
                if (per.hasName()) {
                    if (per.getCategory().equals(category)) {
                        neededPersons.add(per);
                    }
                }
            }

            MultiContactsAdapter adapter = new MultiContactsAdapter(this, neededPersons, isChemeDark(Statics.color1));
            listView.setDivider(null);
            listView.setAdapter(adapter);
            //listView.setSelector(R.drawable.romanblack_multicontacts_custom_background);

            listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    showDetails(arg2, arg1);
                }
            });

            listView.setVisibility(View.VISIBLE);

            if (neededPersons.isEmpty()) {
                listView.setVisibility(View.GONE);
            }

            handler.sendEmptyMessage(HIDE_PROGRESS_DIALOG);

        } catch (Exception ex) {
        }
    }

    /**
     * Starts CantactDetailsActivity to show selected person details.
     * @param position selected person position
     * @param view selected subview of persons ListView
     */
    private void showDetails(int position, View view) {
        try {
            Intent details = new Intent(this, ContactDetailsActivity.class);
            details.putExtra("Widget", widget);
            details.putExtra("person", neededPersons.get(position));
            details.putExtra("single",false);
            details.putExtra("isdark", isChemeDark(Statics.color1));
            details.putExtra("hasschema", PluginData.getInstance().isHasColorSchema());
            startActivity(details);
            overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        } catch (Exception e) {//ErrorLogging
        }
    }

    private void closeActivity() {
        hideProgressDialog();
        finish();
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * Checks if the color is dark.
     * @param backColor the color to check
     * @return true if the color is dark, false otherwise
     */
    private boolean isChemeDark(int backColor) {
        int r = (backColor >> 16) & 0xFF;
        int g = (backColor >> 8) & 0xFF;
        int b = (backColor >> 0) & 0xFF;

        double Y = (0.299 * r + 0.587 * g + 0.114 * b);
        if (Y > 127) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Updates page background after background image was downloaded and decoded.
     */
    private void updateBackground() {
        try {
            root.setBackgroundDrawable(new BitmapDrawable(
                    BitmapFactory.decodeStream(new FileInputStream(cacheBackgroundFile))));
            backgroundLoadede = true;

            handler.sendEmptyMessage(SHOW_PERSONS);
        } catch (FileNotFoundException fNFEx) {
        }
    }

    /**
     * This class creates a background thread to download page background.
     */
    private class BackgroundDownloadTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... arg0) {
            try {
                URL imageUrl = new URL(arg0[0]);
                BufferedInputStream bis = new BufferedInputStream(imageUrl.openConnection().getInputStream());
                ByteArrayBuffer baf = new ByteArrayBuffer(32);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }

                File cacheAvatar = new File(cacheBackgroundFile);
                if (!cacheAvatar.exists()) {
                    new File(cachePath).mkdirs();
                    cacheAvatar.createNewFile();
                }

                FileOutputStream fos = new FileOutputStream(cacheAvatar);
                fos.write(baf.toByteArray());
                fos.close();

                publishProgress();
            } catch (Exception e) {
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            updateBackground();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
