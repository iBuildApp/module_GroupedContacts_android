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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.appbuilder.sdk.android.AppBuilderModuleMain;
import com.appbuilder.sdk.android.StartUpActivity;
import com.appbuilder.sdk.android.Utils;
import com.appbuilder.sdk.android.Widget;
import com.ibuildapp.romanblack.MultiContactsPlugin.adapters.GroupContactsAdapter;
import com.ibuildapp.romanblack.MultiContactsPlugin.adapters.MultiContactsAdapter;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.Contact;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.Person;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.EntityParser;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.PluginData;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.Statics;
import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Main module class. Module entry point. Represents multicontacts widget.
 */
@StartUpActivity(moduleName = "GroupedContacts")
public class MultiContactsPlugin extends AppBuilderModuleMain implements View.OnFocusChangeListener{

    private static final String TAG = "com.ibuildapp.MultiContactsPlugin";

    private static int positionLayoutCenterX;
    private static int positionLayoutLeftX;
    private static boolean isLeftPostition = false;

    private final int INITIALIZATION_FAILED = 0;
    private final int LOADING_ABORTED = 1;
    private final int SHOW_DATA = 2;
    private final int HIDE_PROGRESS_DIALOG = 3;
    private final int THERE_IS_NO_PERSONS = 5;
    private final int SET_ROOT_BACKGROUND = 6;
    private final int RESET_BACKGROUND = 7;
    private final int ONLY_ONE_CONTACT = 1000;
    private boolean backgroundLoaded = false;
    private String cachePath = "";
    private String cacheBackgroundFile = "";
    private String cacheAvavtarFile = "";
    private ListView listView = null;
    private ProgressDialog progressDialog = null;
    private LinearLayout root = null;
    ImageView avatarImage;
    Dialog callDialog;
    private Person person = null;
    private Widget widget = null;
    private ArrayList<Person> persons = null;
    private ArrayList<Person> neededPersons = null;
    List<String> neededCategories;
    private Resources resources;
    private EntityParser parser;
    private EditText searchContactsEditText;
    private TextView noFoundText;
    private ImageView clearSearch;
    private RelativeLayout inputSearchLayout;
    private LinearLayout multicontactsSearchLayout;
    private LinearLayout moveLayout;
    private ArrayList<Contact> neededContacts;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case INITIALIZATION_FAILED: {
                    Toast.makeText(MultiContactsPlugin.this, R.string.alert_cannot_init, Toast.LENGTH_LONG).show();
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
                case SHOW_DATA: {
                    try {
                        showGroups();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                    PluginData.getInstance().setHasColorSchema(parser.isHasColorSchema());
                    if (prepareUI()) {
                        return;
                    }
                    if (parser.isHasColorSchema()) {
                        root.setBackgroundColor(Statics.color1);
                        backgroundLoaded = true;
                    } else {
                        if (widget.isBackgroundColor()) {
                            if (widget.getBackgroundColor() != Color.TRANSPARENT) {
                                root.setBackgroundColor(widget.getBackgroundColor());
                                backgroundLoaded = true;
                            }
                        } else if (widget.isBackgroundURL()) {
                            try {
                                cacheBackgroundFile = cachePath + "/" + Utils.md5(widget.getBackgroundURL());
                                File backgroundFile = new File(cacheBackgroundFile);
                                if (backgroundFile.exists()) {
                                    root.setBackgroundDrawable(new BitmapDrawable(
                                            BitmapFactory.decodeStream(new FileInputStream(backgroundFile))));
                                    backgroundLoaded = true;
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
                                root.setBackgroundDrawable(new BitmapDrawable(am.open(widget.getBackgroundURL())));
                                backgroundLoaded = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (backgroundLoaded) {
                        handler.sendEmptyMessage(SHOW_DATA);
                    }
                }
                break;

                case THERE_IS_NO_PERSONS: {
                    Toast.makeText(MultiContactsPlugin.this,
                            R.string.romanblack_multicontacts_alert_no_persons,
                            Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                    finish();
                }
                break;
            }
        }
    };

    @Override
    public void create() {
        try {
            Intent currentIntent = getIntent();
            Bundle store = currentIntent.getExtras();
            widget = (Widget) store.getSerializable("Widget");

            if (widget == null) {
                handler.sendEmptyMessage(INITIALIZATION_FAILED);
                return;
            }

            if (widget.getPluginXmlData().length() == 0) {
                if (currentIntent.getStringExtra("WidgetFile").length() == 0) {
                    handler.sendEmptyMessageDelayed(INITIALIZATION_FAILED, 100);
                    return;
                }
            }

            new Thread() {
                @Override
                public void run() {
                    try {//ErrorLogging
                        if (widget.getPluginXmlData() != null) {
                            if (widget.getPluginXmlData().length() > 0) {
                                parser = new EntityParser(widget.getPluginXmlData());
                            } else {
                                String xmlData = readXmlFromFile(getIntent().getStringExtra("WidgetFile"));
                                parser = new EntityParser(xmlData);
                            }
                        } else {
                            String xmlData = readXmlFromFile(getIntent().getStringExtra("WidgetFile"));
                            parser = new EntityParser(xmlData);
                        }
                        persons = parser.parse();

                        Statics.color1 = parser.getColor1();
                        Statics.color2 = parser.getColor2();
                        Statics.color3 = parser.getColor3();
                        Statics.color4 = parser.getColor4();
                        Statics.color5 = parser.getColor5();

                        PluginData.getInstance().setPersons(parser.getPersons());
                        // set background color/image/httpimage

                        handler.sendEmptyMessage(SET_ROOT_BACKGROUND);
                    } catch (Exception e) {
                    }
                }
            }.start();
        } catch (Exception e) {
        }
    }

    /**
     * This method using when module data is too big to put in Intent
     *
     * @param fileName - xml module data file name
     * @return xml module data
     */
//    private String readXmlFromFile(String fileName) {
//        Context context = this.getApplicationContext();
//        StringBuilder stringBuilder = new StringBuilder();
//        String line;
//        BufferedReader in = null;
//
//        try {
//            in = new BufferedReader(new FileReader(new File(fileName)));
//            while ((line = in.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//        } catch (FileNotFoundException e) {
//        } catch (IOException e) {
//        }
//
//        return stringBuilder.toString();
//    }

    /**
     * Prepares page UI or starts ContactDetailsActivity if there is only one
     * person.
     *
     * @return true if there is only one person, false othrwise
     */
    private boolean prepareUI() {
        if (persons.size() == 1) {
            try {
                Intent details = new Intent(this, ContactDetailsActivity.class);
                details.putExtra("Widget", widget);
                details.putExtra("person", persons.get(0));
                details.putExtra("single", true);
                details.putExtra("isdark", Utils.isChemeDark(Statics.color1));
                details.putExtra("hasschema", PluginData.getInstance().isHasColorSchema());
                details.putExtra("homebtn", true);
                finish();
                startActivity(details);
                return true;
            } catch (Exception e) {
            }
        }
        setContentView(R.layout.romanblack_multicontacts_main);

        setTopBarTitle(widget.getTitle());
        setTopBarLeftButtonText(getResources().getString(R.string.common_home_upper), true, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });

        clearSearch = (ImageView) findViewById(R.id.multicontacts_delete_search);

        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchContactsEditText.setText("");
                noFoundText.setVisibility(View.GONE);
            }
        });
        clearSearch.setVisibility(View.INVISIBLE);

        resources = getResources();
        inputSearchLayout = (RelativeLayout) findViewById(R.id.inputSearchLayout);
        multicontactsSearchLayout = (LinearLayout) findViewById(R.id.multicontacts_search_layout);
        moveLayout = (LinearLayout) findViewById(R.id.move_layout);

        if (Statics.color1 == Color.parseColor("#FFFFFF"))
            multicontactsSearchLayout.setBackgroundResource(R.drawable.backwithborderblack);
        else
            multicontactsSearchLayout.setBackgroundResource( R.drawable.backwithborder);
        multicontactsSearchLayout.setVisibility(View.GONE);
        searchContactsEditText = (EditText) findViewById(R.id.inputSearch);
        if (getPackageName().endsWith("p638839")) {
            searchContactsEditText.setHint("Search by Location");
        }
        searchContactsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
        searchContactsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.length() == 0) {
                    List<String> cats = PluginData.getInstance().getCategories();

                    clearSearch.setVisibility(View.INVISIBLE);
                    noFoundText.setVisibility(View.GONE);

                    if (cats.size() > 1) {
                        GroupContactsAdapter adapter =
                                new GroupContactsAdapter(
                                        MultiContactsPlugin.this,
                                        cats,
                                        Utils.isChemeDark(Statics.color1));

                        listView.setSelector(R.drawable.romanblack_multicontacts_custom_background);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new OnItemClickListener() {
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                showCategoryPersons(arg2, arg1);
                            }
                        });
                    } else {
                        multicontactsSearchLayout.setVisibility(View.GONE);
                        neededPersons = new ArrayList<Person>();
                        neededPersons.addAll(persons);
                        MultiContactsAdapter adapter =
                                new MultiContactsAdapter(
                                        MultiContactsPlugin.this,
                                        neededPersons,
                                        Utils.isChemeDark(Statics.color1));

                        listView.setSelector(R.drawable.romanblack_multicontacts_custom_background);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new OnItemClickListener() {
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                showPersonDetails(arg2, arg1);
                            }
                        });
                        listView.setVisibility(View.VISIBLE);
                    }

                    listView.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.VISIBLE);
                    if (!isLeftPostition){
                        moveToLeft();
                        isLeftPostition = true;
                    }

                    List<Person> persons = PluginData.getInstance().searchByString(charSequence.toString());
                    neededPersons = new ArrayList<Person>();
                    neededPersons.addAll(persons);
                    if (neededPersons.size() == 0) {
                        noFoundText.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.GONE);
                    } else {
                        MultiContactsAdapter adapter =
                                new MultiContactsAdapter(
                                        MultiContactsPlugin.this,
                                        neededPersons,
                                        Utils.isChemeDark(Statics.color1));

                        listView.setSelector(R.drawable.romanblack_multicontacts_custom_background);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new OnItemClickListener() {
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                showPersonDetails(arg2, arg1);
                            }
                        });

                        listView.setDivider(null);
                        listView.setVisibility(View.VISIBLE);
                        noFoundText.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        searchContactsEditText.clearFocus();
        cachePath = widget.getCachePath() + "/contacts-" + widget.getOrder();

        root = (LinearLayout) findViewById(R.id.romanblack_multicontacts_main_root);
        listView = (ListView) findViewById(R.id.romanblack_multicontacts_list);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setDivider(null);

        noFoundText = (TextView) findViewById(R.id.no_found_text);
        noFoundText.setTextColor(Statics.color3);

        progressDialog = ProgressDialog.show(this, null, getString(R.string.common_loading_upper), true);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface di) {
                handler.sendEmptyMessage(LOADING_ABORTED);
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return false;
    }

    private void moveToCenter() {
        TranslateAnimation animation = new TranslateAnimation(0, positionLayoutCenterX,
                0.0f, 0.0f);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveLayout.layout(positionLayoutCenterX,0,moveLayout.getWidth(),moveLayout.getHeight() );
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        moveLayout.startAnimation(animation);
    }

    public void moveToLeft(){
        positionLayoutCenterX = moveLayout.getLeft();
        positionLayoutLeftX = inputSearchLayout.getLeft();
        TranslateAnimation animation = new TranslateAnimation(0, -positionLayoutCenterX,
                0.0f, 0.0f);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveLayout.layout(0,0,moveLayout.getWidth(),moveLayout.getHeight() );
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        moveLayout.startAnimation(animation);
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
     * Shows group list or starts ContactDetailsActivity if there is only one
     * person.
     *
     * @throws IOException
     */
    private void showGroups() throws IOException {
        if (persons.size() == 1) {
            try {
                Intent details = new Intent(this, ContactDetailsActivity.class);
                details.putExtra("Widget", widget);
                details.putExtra("person", persons.get(0));
                details.putExtra("single", true);
                details.putExtra("isdark", Utils.isChemeDark(Statics.color1));
                details.putExtra("hasschema", PluginData.getInstance().isHasColorSchema());
                startActivity(details);
                finish();
                return;
            } catch (Exception e) {
            }
        } else {
            try {
                List<String> categories = new ArrayList<String>();
                for (Person p : persons) {
                    String c = p.getCategory();
                    if (c != null && !categories.contains(c)) {
                        categories.add(c);
                    }
                }

                if (categories.size() <= 1) {
                    multicontactsSearchLayout.setVisibility(View.GONE);
                    neededPersons = new ArrayList<Person>();
                    neededPersons.addAll(persons);
                    MultiContactsAdapter adapter = new MultiContactsAdapter(this,
                            neededPersons,
                            Utils.isChemeDark(Statics.color1));
                    listView.setSelector(R.drawable.romanblack_multicontacts_custom_background);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            showPersonDetails(arg2, arg1);
                        }
                    });
                    listView.setVisibility(View.VISIBLE);
                } else {
                    multicontactsSearchLayout.setVisibility(View.VISIBLE);
                    neededCategories = new ArrayList<String>();
                    neededCategories.addAll(categories);

                    GroupContactsAdapter adapter = new GroupContactsAdapter(this,
                            neededCategories,
                            Utils.isChemeDark(Statics.color1));
                    listView.setAdapter(adapter);
                    listView.setSelector(R.drawable.romanblack_multicontacts_custom_background);
                    listView.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            showCategoryPersons(arg2, arg1);
                        }
                    });
                    listView.setVisibility(View.VISIBLE);
                }
                handler.sendEmptyMessage(HIDE_PROGRESS_DIALOG);
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Starts MultiContactsActivity to show persons of selected category.
     *
     * @param position selected category position
     * @param view     seletcted subview of categories ListView
     */
    private void showCategoryPersons(int position, View view) {
        try {
            Intent details = new Intent(this, MultiContactsActivity.class);
            details.putExtra("Widget", widget);
            details.putExtra("category", neededCategories.get(position));
            details.putExtra("single", false);
            details.putExtra("isdark", Utils.isChemeDark(Statics.color1));
            details.putExtra("hasschema", parser.isHasColorSchema());
            startActivity(details);

        } catch (Exception e) {//ErrorLogging
        }
    }

    /**
     * Starts ContactDetailsActivity to show details of selected person.
     *
     * @param position selected person position
     * @param view     seletcted subview of persons ListView
     */
    private void showPersonDetails(int position, View view) {
        try {
            Intent details = new Intent(this, ContactDetailsActivity.class);
            details.putExtra("Widget", widget);
            details.putExtra("person", neededPersons.get(position));
            details.putExtra("single", false);
            details.putExtra("isdark", Utils.isChemeDark(Statics.color1));
            details.putExtra("hasschema", parser.isHasColorSchema());
            startActivity(details);
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
     * Updates the page background after background image was downloaded and
     * decoded.
     */
    private void updateBackground() {
        try {
            root.setBackgroundDrawable(new BitmapDrawable(
                    BitmapFactory.decodeStream(new FileInputStream(cacheBackgroundFile))));
            backgroundLoaded = true;
            handler.sendEmptyMessage(SHOW_DATA);
        } catch (FileNotFoundException fNFEx) {
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
      /*  int i = v.getId();
        if (i == R.id.inputSearch) {
            if (isLeftPostition){
                moveToCenter();
                isLeftPostition = false;
            }
            else {
                moveToLeft();
                isLeftPostition = true;
            }
        }*/
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
