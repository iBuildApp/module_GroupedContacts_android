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
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.appbuilder.sdk.android.AppBuilderModuleMain;
import com.appbuilder.sdk.android.DialogSharing;
import com.appbuilder.sdk.android.Utils;
import com.appbuilder.sdk.android.Widget;
import com.bumptech.glide.Glide;
import com.ibuildapp.romanblack.MultiContactsPlugin.adapters.ContactDetailsAdapter;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.Contact;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.Person;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.Statics;
import com.ibuildapp.romanblack.MultiContactsPlugin.views.CallDialog;
import com.ibuildapp.romanblack.MultiContactsPlugin.views.ShadowDrawable;

import org.apache.http.util.ByteArrayBuffer;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This activity represents person details page.
 */
public class ContactDetailsActivity extends AppBuilderModuleMain {

    private static final String PARAM_SEND_MAIL = "send_mail";
    private static final String PARAM_SEND_SMS = "send_sms";
    private static final String PARAM_ADD_CONTACT = "add_contact";

    private static final int SHARE_EMAIL_ID = 0;
    private static final int SHARE_SMS_ID = 1;
    private boolean isOnline = false;
    private Person person = null;
    private Widget widget = null;
    private ProgressDialog progressDialog = null;
    private ListView listcont;
    private ImageView avatarImage;
    private LinearLayout root;
    static public String mBackground;
    private ArrayList<Contact> contacts;
    private ArrayList<Contact> neededContacts;
    Dialog callDialog;
    private String cachePath = "";
    private String cacheAvavtarFile = "";
    private String cacheBackgroundFile = "";
    private boolean hasSchema = false;
    final private int INITIALIZATION_FAILED = 3;
    final private int NEED_INTERNET_CONNECTION = 4;
    final private int HIDE_PROGRESS_DIALOG = 5;
    final private int THERE_IS_NO_CONTACT_DATA = 6;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case INITIALIZATION_FAILED: {
                    Toast.makeText(ContactDetailsActivity.this, R.string.alert_cannot_init, Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            hideProgress();
                            finish();
                        }
                    }, 5000);
                }
                break;
                case NEED_INTERNET_CONNECTION: {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(ContactDetailsActivity.this, R.string.alert_no_internet, Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            hideProgress();
                            finish();
                        }
                    }, 5000);
                }
                break;
                case HIDE_PROGRESS_DIALOG: {
                    hideProgress();
                }
                break;
                case THERE_IS_NO_CONTACT_DATA: {
                    Toast.makeText(ContactDetailsActivity.this, R.string.romanblack_multicontacts_alert_no_contact_data, Toast.LENGTH_LONG).show();
                    hideProgress();
                    finish();
                }
                break;
            }
        }
    };
    private View headSeparator;
    private View bottomSeparator;
    private View imageBottom;
    private TextView personName;

    @Override
    public void create() {
        try {
            setContentView(R.layout.romanblack_multicontacts_details);

            Intent currentIntent = getIntent();
            Bundle store = currentIntent.getExtras();
            widget = (Widget) store.getSerializable("Widget");
            if (widget == null) {
                handler.sendEmptyMessageDelayed(INITIALIZATION_FAILED, 100);
                return;
            }

            person = (Person) store.getSerializable("person");
            if (person == null) {
                handler.sendEmptyMessageDelayed(INITIALIZATION_FAILED, 100);
                return;
            }
            setTopBarTitle(widget.getTitle());

            Boolean single = currentIntent.getBooleanExtra("single", true);

            setTopBarLeftButtonTextAndColor(single? getResources().getString(R.string.common_home_upper): getResources().getString(R.string.common_back_upper),
                    getResources().getColor(android.R.color.black), true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                    return;
                }
            });
            setTopBarTitleColor(getResources().getColor(android.R.color.black));
            setTopBarBackgroundColor(Statics.color1);

            if((Boolean.TRUE.equals(widget.getParameter(PARAM_SEND_MAIL))) ||
                    (Boolean.TRUE.equals(widget.getParameter(PARAM_SEND_SMS))) ||
                    (Boolean.TRUE.equals(widget.getParameter(PARAM_SEND_SMS)))) {

                ImageView shareButton = (ImageView) getLayoutInflater().inflate(R.layout.romanblack_multicontacts_share_btn, null);
                shareButton.setLayoutParams(new LinearLayout.LayoutParams((int) (29 * getResources().getDisplayMetrics().density), (int) (39 * getResources().getDisplayMetrics().density)));
                shareButton.setColorFilter(navBarDesign.itemDesign.textColor);
                setTopBarRightButton(shareButton, getString(R.string.multicontacts_list_share),  new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogSharing.Configuration.Builder sharingDialogBuilder = new DialogSharing.Configuration.Builder();

                        if(Boolean.TRUE.equals(widget.getParameter(PARAM_SEND_MAIL)))
                            sharingDialogBuilder.setEmailSharingClickListener(new DialogSharing.Item.OnClickListener() {
                                @Override
                                public void onClick() {
                                    String message = getContactInfo();
                                    Intent email = new Intent(Intent.ACTION_SEND);
                                    email.putExtra(Intent.EXTRA_TEXT, message);
                                    email.setType("message/rfc822");
                                    startActivity(Intent.createChooser(email, getString(R.string.choose_email_client)));
                                }
                            });

                        if(Boolean.TRUE.equals(widget.getParameter(PARAM_SEND_SMS)))
                            sharingDialogBuilder.setSmsSharingClickListener(new DialogSharing.Item.OnClickListener() {
                                @Override
                                public void onClick() {
                                    String message = getContactInfo();

                                    try {
                                        Utils.sendSms(ContactDetailsActivity.this, message);
                                    } catch (ActivityNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        if(Boolean.TRUE.equals(widget.getParameter(PARAM_ADD_CONTACT)))
                            sharingDialogBuilder.addCustomListener(R.string.multicontacts_add_to_phonebook, R.drawable.multicontacts_add_to_contacts, true, new DialogSharing.Item.OnClickListener() {
                                @Override
                                public void onClick() {
                                    createNewContact(
                                            person.getName(),
                                            person.getPhone(),
                                            person.getEmail());
                                }
                            });

                        showDialogSharing(sharingDialogBuilder.build());
                    }
                });

            }

            hasSchema = store.getBoolean("hasschema");
            cachePath = widget.getCachePath() + "/contacts-" + widget.getOrder();
            if (person.hasAvatar()) {
                cacheAvavtarFile = cachePath + "/" + Utils.md5(person.getAvatarUrl());
            }

            contacts = person.getContacts();

            if (widget.getTitle().length() > 0) {
                setTitle(widget.getTitle());
            }

            root = (LinearLayout) findViewById(R.id.romanblack_multicontacts_details_root);

            if (hasSchema) {
                root.setBackgroundColor(Statics.color1);
            } else {
                if (widget.isBackgroundColor()) {
                } else if (widget.isBackgroundURL()) {
                    cacheBackgroundFile = cachePath + "/" + Utils.md5(widget.getBackgroundURL());
                    File backgroundFile = new File(cacheBackgroundFile);
                    if (backgroundFile.exists()) {
                        root.setBackgroundDrawable(new BitmapDrawable(
                                BitmapFactory.decodeStream(new FileInputStream(backgroundFile))));
                    } else {
                        BackgroundDownloadTask dt = new BackgroundDownloadTask();
                        dt.execute(widget.getBackgroundURL());
                    }
                } else if (widget.isBackgroundInAssets()) {
                    AssetManager am = this.getAssets();
                    root.setBackgroundDrawable(new BitmapDrawable(
                            am.open(widget.getBackgroundURL())));
                }
            }

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null && ni.isConnectedOrConnecting()) {
                isOnline = true;
            }

            if (contacts != null) {
                avatarImage = (ImageView) findViewById(R.id.romanblack_multicontacts_details_avatar);

                avatarImage.setImageResource(R.drawable.gc_profile_avatar);
                if (person.hasAvatar() && isOnline) {
                    avatarImage.setVisibility(View.VISIBLE);
                    Glide.with(this).load(person.getAvatarUrl()).dontAnimate().into(avatarImage);
                } else {
                    avatarImage.setVisibility(View.VISIBLE);
                    avatarImage.setImageResource(R.drawable.gc_profile_avatar);
                }

                String name = "";
                neededContacts = new ArrayList<>();
                for (Iterator<Contact> iter = contacts.iterator(); iter.hasNext(); ) {
                    Contact con = iter.next();
                    if ((con.getType() == 5) || (con.getDescription().length() == 0)) {
                    } else {
                        if (con.getType() == 0){
                            name = con.getDescription();
                        }else
                        neededContacts.add(con);
                    }
                }

                if (neededContacts.isEmpty()) {
                    handler.sendEmptyMessage(THERE_IS_NO_CONTACT_DATA);
                    return;
                }

                headSeparator = findViewById(R.id.gc_head_separator);
                bottomSeparator = findViewById(R.id.gc_bottom_separator);
                imageBottom = findViewById(R.id.gc_image_bottom_layout);
                personName = (TextView) findViewById(R.id.gc_details_description);

                if("".equals(name))
                    personName.setVisibility(View.GONE);
                else {
                    personName.setVisibility(View.VISIBLE);
                    personName.setText(name);
                    personName.setTextColor(Statics.color3);
                }
                if (Statics.isLight) {
                    headSeparator.setBackgroundColor(Color.parseColor("#4d000000"));
                    bottomSeparator.setBackgroundColor(Color.parseColor("#4d000000"));
                } else {
                    headSeparator.setBackgroundColor(Color.parseColor("#4dFFFFFF"));
                    bottomSeparator.setBackgroundColor(Color.parseColor("#4dFFFFFF"));
                }

                if (Statics.color1 == Color.WHITE)
                    imageBottom.setBackgroundColor(Color.parseColor("#33000000"));
                else
                    imageBottom.setBackgroundColor(Color.parseColor("#66FFFFFF"));

                listcont = (ListView) findViewById(R.id.romanblack_multicontacts_details_listwiew);
                listcont.setDivider(null);

                ContactDetailsAdapter adapter = new ContactDetailsAdapter(ContactDetailsActivity.this,
                        R.layout.romanblack_multicontacts_details_item,
                        neededContacts, isChemeDark(Statics.color1));
                listcont.setAdapter(adapter);
                listcont.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                        listViewonItemClick(position);
                    }
                });
            }

            if (widget.hasParameter("add_contact")) {
                HashMap<String, String> hm = new HashMap<String, String>();
                for (int i = 0; i < contacts.size(); i++) {
                    switch (contacts.get(i).getType()) {
                        case 0: {
                            hm.put("contactName", contacts.get(i).getDescription());
                        }
                        break;
                        case 1: {
                            hm.put("contactNumber", contacts.get(i).getDescription());
                        }
                        break;
                        case 2: {
                            hm.put("contactEmail", contacts.get(i).getDescription());
                        }
                        break;
                        case 3: {
                            hm.put("contactSite", contacts.get(i).getDescription());
                        }
                        break;
                    }
                }
                addNativeFeature(NATIVE_FEATURES.ADD_CONTACT, null, hm);
            }
            if (widget.hasParameter("send_sms")) {
                HashMap<String, String> hm = new HashMap<String, String>();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < contacts.size(); i++) {
                    sb.append(contacts.get(i).getDescription());
                    if (i < contacts.size() - 1) {
                        sb.append(", ");
                    }
                }
                hm.put("text", sb.toString());
                addNativeFeature(NATIVE_FEATURES.SMS, null, hm);
            }
            if (widget.hasParameter("send_mail")) {
                HashMap<String, CharSequence> hm = new HashMap<String, CharSequence>();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < contacts.size(); i++) {
                    switch (contacts.get(i).getType()) {
                        case 0: {
                            sb.append("Name: ");
                        }
                        break;
                        case 1: {
                            sb.append("Phone: ");
                        }
                        break;
                        case 2: {
                            sb.append("Email: ");
                        }
                        break;
                        case 3: {
                            sb.append("Site: ");
                        }
                        break;
                        case 4: {
                            sb.append("Address: ");
                        }
                        break;
                    }
                    sb.append(contacts.get(i).getDescription());
                    sb.append("<br/>");
                }

                if (widget.isHaveAdvertisement()) {
                    sb.append("<br>\n (sent from <a href=\"http://ibuildapp.com\">iBuildApp</a>)");
                }

                hm.put("text", sb.toString());
                hm.put("subject", "Contacts");
                addNativeFeature(NATIVE_FEATURES.EMAIL, null, hm);
            }

        } catch (Exception e) {
        }
    }

    private void addShadow(ImageView view) {
        try {
            Drawable drawable = view.getDrawable();

            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            if(Build.VERSION.SDK_INT >= 11)
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

            view.setImageDrawable(new ShadowDrawable(getResources(), bitmap));
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void pause() {
    }

    /**
     * Opens a contact detial pare depending on contact type.
     *
     * @param position contact position
     */
    private void listViewonItemClick(int position) {
        try {//ErrorLogging

            int type = neededContacts.get(position).getType();
            switch (type) {
                case 0: {
                }
                break;
                case 1: {
                    final String phoneNumber = neededContacts.get(position).getDescription();

                    new CallDialog(this, phoneNumber, new CallDialog.ActionListener() {
                        @Override
                        public void onCall(DialogInterface dialog) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phoneNumber));
                            startActivity(callIntent);
                            dialog.dismiss();
                        }

                        @Override
                        public void onAddContact(DialogInterface dialog) {
                            createNewContact(
                                    person.getName(),
                                    person.getPhone(),
                                    person.getEmail());
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    }).show();
                }
                break;
                case 2: {
                    String email = neededContacts.get(position).getDescription();
                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("text/html");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
                    startActivity(emailIntent);
                }
                break;
                case 3: {
                    String url = neededContacts.get(position).getDescription();

                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }

                    Intent intent = new Intent(this, ContactsWebActivity.class);
                    intent.putExtra("link", url);
                    startActivity(intent);
                }
                break;
                case 4: {
                    Intent intent = new Intent(this, ContactsMapActivity.class);
                    intent.putExtra("person", person);
                    startActivity(intent);
                }
                break;
            }

        } catch (Exception e) {
        }
    }

    /**
     * Saves given contact to device phone book.
     *
     * @param name         contact name to save
     * @param MobileNumber contact mobile number to save
     * @param emailID      contact email address to save
     */
    private void createNewContact(String name, String MobileNumber, String emailID) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (name != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            name).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Email
        if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }


        // Asking the Contact provider to create a new contact
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(this, getResources().getString(R.string.romanblack_multicontacts_alert_contact_added), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.romanblack_multicontacts_alert_contact_added_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void closeActivity() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        finish();
    }

    private void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * Updates user avatar after it was downloaded.
     */
    private void updateAvatar() {
        try {
            avatarImage.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(cacheAvavtarFile)));
            addShadow(avatarImage);
        } catch (FileNotFoundException fNFEx) {
        }
    }

    /**
     * Checks if the color is dark.
     *
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
     * This class creates a background thread to download person avatar.
     */
    private class ImageDownloadTask extends AsyncTask<Person, String, Void> {

        @Override
        protected Void doInBackground(Person... arg0) {
            try {
                URL imageUrl = new URL(arg0[0].getAvatarUrl());
                BufferedInputStream bis = new BufferedInputStream(imageUrl.openConnection().getInputStream());
                ByteArrayBuffer baf = new ByteArrayBuffer(32);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }

                File cacheAvatar = new File(cacheAvavtarFile);
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

            updateAvatar();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    /**
     * Updates activity background.
     */
    private void updateBackground() {
        try {
            root.setBackgroundDrawable(new BitmapDrawable(
                    BitmapFactory.decodeStream(new FileInputStream(cacheBackgroundFile))));
        } catch (FileNotFoundException fNFEx) {
        }
    }

    /**
     * This menu contains "share via SMS" and "share via Email" buttons.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // set menu items
//        menu.add(0, SHARE_EMAIL_ID, Menu.NONE, getString(R.string.multicontacts_share_via_email));
//        menu.add(0, SHARE_SMS_ID, Menu.NONE, getString(R.string.share_contact_via_sms));

        return false;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SHARE_EMAIL_ID: {
                String message = getContactInfo();
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_TEXT, message);
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client"));
                break;
            }
            case SHARE_SMS_ID: {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);

                smsIntent.putExtra("sms_body", getContactInfo());
                smsIntent.putExtra("address", "");
                smsIntent.setType("vnd.android-dir/mms-sms");

                startActivity(smsIntent);
                break;
            }
        }

        return false;
    }

    /**
     * Returns string person info.
     *
     * @return string person info
     */
    private String getContactInfo() {
        String message = "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < contacts.size(); i++) {
            switch (contacts.get(i).getType()) {
                case 0: {
                    sb.append(getString(R.string.multicontacts_name) + ": ");
                }
                break;
                case 1: {
                    sb.append(getString(R.string.multicontacts_phone) + ": ");
                }
                break;
                case 2: {
                    sb.append(getString(R.string.multicontacts_email) + ": ");
                }
                break;
                case 3: {
                    sb.append(getString(R.string.multicontacts_site) + ": ");
                }
                break;
                case 4: {
                    sb.append(getString(R.string.multicontacts_address) + ": ");
                }
                break;
            }
            sb.append(contacts.get(i).getDescription());
            sb.append("\n");
        }

        if (widget.isHaveAdvertisement()) {
            sb.append("\n(sent from iBuildApp.com)");
        }

        message = sb.toString();
        return message;
    }

    /**
     * This class creates a background thread to download background image.
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

                File cacheBackground = new File(cacheBackgroundFile);
                if (!cacheBackground.exists()) {
                    new File(cachePath).mkdirs();
                    cacheBackground.createNewFile();
                }

                FileOutputStream fos = new FileOutputStream(cacheBackground);
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
}