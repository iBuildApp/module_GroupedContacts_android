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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.appbuilder.sdk.android.AppBuilderModule;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.MapItem;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.MapLocation;
import com.ibuildapp.romanblack.MultiContactsPlugin.entities.Person;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.MapBottomPanel;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.MapWebPageCreator;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This activity represents Google Map page for contact with map type.
 */
public class ContactsMapActivity extends AppBuilderModule implements LocationListener {

    private final int INITIALIZATION_FAILED = 0;
    private final int GO_TO_URL = 1;
    private final int SHOW_MAP = 2;
    private final int HIDE_PROGRESS_DIALOG = 3;
    private final int CLOSE_ACTIVITY = 4;
    private final int NO_GPS_SERVICE = 5;
    private final int SEARCH_LOCATION = 6;
    private final int DRAW_ROUTE = 7;
    private final int CHOSE_ROUTE_FINAL = 8;
    private final int SHOW_PROGRESS_DIALOG = 9;
    private String urlToGo = "";
    private String title = "";
    private String htmlSource = "";
    private String address = "";
    private String routeUrl = "";

    private Person person = null;
    private LocationManager locationManager = null;
    private float srcLatitude = 0;
    private float srcLongitude = 0;
    private float dstLatitude = 0;
    private float dstLongitude = 0;
    private MapLocation gpsLocation = null;
    private MapLocation tempLocation = null;
    private MapLocation userLocation = null;
    private ArrayList<MapLocation> locations = new ArrayList<MapLocation>();
    private ArrayList<MapItem> items = null;
    private ProgressDialog progressDialog = null;
    private WebView mapView = null;
    private Button btnMyLocation = null;
    private Button btnDirection = null;
    private Spinner locationSpinner = null;
    private MapBottomPanel mapBottomPanel;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case INITIALIZATION_FAILED: {
                    Toast.makeText(ContactsMapActivity.this, R.string.alert_cannot_init, Toast.LENGTH_LONG).show();
                    closeActivity();
                }
                break;
                case SHOW_PROGRESS_DIALOG: {
                    showProgressDialog();
                }
                break;
                case HIDE_PROGRESS_DIALOG: {
                    hideProgressDialog();
                }
                break;
                case CLOSE_ACTIVITY: {
                    closeActivity();
                }
                break;
                case NO_GPS_SERVICE: {
                    Toast.makeText(ContactsMapActivity.this, R.string.romanblack_multicontacts_alert_gps_not_available, Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                        }
                    }, 5000);
                }
                break;
                case SEARCH_LOCATION: {
                    Toast.makeText(ContactsMapActivity.this, R.string.romanblack_multicontacts_alert_wait_for_gps,
                            Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                        }
                    }, 7000);
                }
                break;
                case DRAW_ROUTE: {
                    startRoute();
                }
                break;
                case CHOSE_ROUTE_FINAL: {
                    choseRouteFinal();
                }
                break;
                case SHOW_MAP: {
                    showMap();
                }
                break;

                case GO_TO_URL: {
                    Intent intent = new Intent(ContactsMapActivity.this, ContactsWebActivity.class);
                    intent.putExtra("link", urlToGo);
                    startActivity(intent);

                }
            }
        }
    };

    @Override
    public void resume() {
        super.resume();

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
        if (locationSpinner != null) {
            locationSpinner.setSelection(0);
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void create() {
        try {//ErrorLogging

            // disable activity header
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            setContentView(R.layout.romanblack_multicontacts_mapweb);
            setTitle("Google Map");

            // checking internet connection
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null && ni.isConnectedOrConnecting()) {
                // for storing map and showing progress dialog
                mapView = (WebView) findViewById(R.id.romanblack_multicontacts_mapweb_webview);
                mapView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                mapView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        handler.sendEmptyMessage(HIDE_PROGRESS_DIALOG);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);

                        handler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
                    }
                });

                mapView.getSettings().setJavaScriptEnabled(true);
//                mapView.getSettings().setPluginsEnabled(true);
                mapView.getSettings().setGeolocationEnabled(true);

                mapView.addJavascriptInterface(new JavaScriptInterface(), "googleredirect");
                person = (Person) getIntent().getSerializableExtra("person");
                routeUrl = (String) getIntent().getStringExtra("url");

                if (TextUtils.isEmpty(routeUrl))
                    address = person.getAddress();

                // Mylocation Button handler
                btnMyLocation = (Button) findViewById(R.id.romanblack_multicontacts_mapweb_back_to_my_location);
                btnMyLocation.setVisibility(View.GONE);
                btnMyLocation.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {

                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            handler.sendEmptyMessage(NO_GPS_SERVICE);
                            return;
                        }

                        if (gpsLocation == null) {
                            if (tempLocation == null) {
                                handler.sendEmptyMessage(SEARCH_LOCATION);
                                return;
                            } else {
                                userLocation = tempLocation;
                            }
                        } else {
                            tempLocation = gpsLocation;
                            userLocation = tempLocation;
                        }

                        srcLatitude = userLocation.getLatitude();
                        srcLongitude = userLocation.getLongitude();
                        srcLatitude = srcLatitude / 1000000;
                        srcLongitude = srcLongitude / 1000000;
                        BigDecimal lat = new BigDecimal(srcLatitude);
                        lat = lat.setScale(6, BigDecimal.ROUND_HALF_UP);
                        BigDecimal lon = new BigDecimal(srcLongitude);
                        lon = lon.setScale(6, BigDecimal.ROUND_HALF_UP);
                        mapView.loadUrl("javascript:backToMyLocation(" + lat.toString() + "," + lon.toString() + ")");
                    }
                });

                // ShowDirection Button handler
                btnDirection = (Button) findViewById(R.id.romanblack_multicontacts_mapweb_user_direction);
                btnDirection.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        try {//ErrorLogging
                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                handler.sendEmptyMessage(NO_GPS_SERVICE);
                                return;
                            }
                            if (gpsLocation == null) {
                                if (tempLocation == null) {
                                    handler.sendEmptyMessage(SEARCH_LOCATION);
                                    return;
                                } else {
                                    userLocation = tempLocation;
                                }
                            } else {
                                tempLocation = gpsLocation;
                                userLocation = tempLocation;
                            }

                            srcLatitude = userLocation.getLatitude();
                            srcLongitude = userLocation.getLongitude();

                            if (locations.isEmpty()) {
                            } else if (locations.size() == 1) {
                                dstLatitude = locations.get(0).getLatitude();
                                dstLongitude = locations.get(0).getLongitude();
                                handler.sendEmptyMessage(DRAW_ROUTE);
                            } else {
                                handler.sendEmptyMessage(CHOSE_ROUTE_FINAL);
                            }

                        } catch (Exception e) {
                        }
                    }
                });

                mapBottomPanel = (MapBottomPanel) findViewById(R.id.romanblack_multicontacts_mapweb_bottom_panel);

                // obtain locatonManager object
                locationManager = (LocationManager) ContactsMapActivity.this.
                        getSystemService(LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    handler.sendEmptyMessage(NO_GPS_SERVICE);
                }

                // show progress dialog
                progressDialog = ProgressDialog.show(this, "", getString(R.string.common_loading_upper));
                progressDialog.setCancelable(true);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface arg0) {
                        handler.sendEmptyMessage(CLOSE_ACTIVITY);
                    }
                });

                if (!TextUtils.isEmpty(routeUrl)) {
                    mapBottomPanel.setVisibility(View.GONE);
                    mapView.loadUrl(routeUrl);
                    return;
                }


                new Thread(new Runnable() {
                    public void run() {

                        try {//ErrorLogging
                            items = new ArrayList<MapItem>();

                            URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address.replaceAll("\n", " ")) + "&sensor=true");

                            String json = "";
                            String line = "";
                            StringBuilder s = new StringBuilder();

                            BufferedReader rd = new BufferedReader(new InputStreamReader(url.openStream()));
                            try {
                                while ((line = rd.readLine()) != null) {
                                    s.append(line);
                                    s.append("\n");
                                }
                                json = s.toString();
                            } catch (Exception e) {
                                handler.sendEmptyMessage(INITIALIZATION_FAILED);
                                return;
                            }

                            line = null;
                            s = null;

                            JSONObject mainObject = new JSONObject(json);
                            String status = mainObject.getString("status");

                            if (status.equalsIgnoreCase("ok")) {
                                JSONObject result = mainObject.getJSONArray("results").getJSONObject(0);

                                JSONObject geometry = result.getJSONObject("geometry");

                                JSONObject location = geometry.getJSONObject("location");

                                double latitude = location.getDouble("lat");
                                double longitude = location.getDouble("lng");

                                MapItem mItem = new MapItem();
                                if (person != null) {
                                    mItem.setTitle(person.getName());
                                } else {
                                    mItem.setTitle("address");
                                }
                                String url11 = null;

                                if (person != null) {
                                    if (person.getContacts().get(3) != null) {
                                        url11 = person.getContacts().get(3).getDescription();
                                        if (url11.length() > 0) {
                                            if (!url11.startsWith("http://") && !url11.startsWith("https://")) {
                                                url11 = "http://" + url11;
                                            }
                                        }

                                    }
                                }

                                mItem.setUrl(url11);
                                mItem.setDescription(address);
                                mItem.setSubtitle(person.getPhone() + "\n" + address);
                                mItem.setLatitude(latitude);
                                mItem.setLongitude(longitude);

                                items.add(mItem);

                                Log.d("", "");
                            } else {
                                handler.sendEmptyMessage(INITIALIZATION_FAILED);
                                return;
                            }

                            for (MapItem item : items) {
                                MapLocation location = new MapLocation(item.getLatitude(), item.getLongitude());
                                location.setTitle(item.getTitle());
                                location.setSubtitle(item.getSubtitle());
                                location.setDescription(item.getDescription());

                                locations.add(location);
                            }

                            htmlSource = "";
                            try {
                                // get html source from resources 
                                InputStream is = getResources().openRawResource(R.raw.romanblack_multicontacts_mapweb_page_refreshable);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                int flag = 0;
                                byte buf[] = new byte[512];
                                while ((flag = is.read(buf, 0, 512)) != -1) {
                                    baos.write(buf, 0, flag);
                                    Arrays.fill(buf, (byte) 0);
                                }
                                htmlSource = baos.toString();
                            } catch (IOException iOEx) {
                                Log.e("", "");
                                handler.sendEmptyMessage(INITIALIZATION_FAILED);
                            }

                            htmlSource = MapWebPageCreator.createMapPage(htmlSource, items);

                            handler.sendEmptyMessage(SHOW_MAP);

                        } catch (Exception e) {
                            handler.sendEmptyMessage(INITIALIZATION_FAILED);
                        }

                    }
                }).start();

            } else {
                this.finish();
                Toast.makeText(getApplicationContext(), R.string.alert_no_internet, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
        }
    }

    /**
     * Chose final destination to draw route.
     */
    private void choseRouteFinal() {
        try {//ErrorLogging

            if (locationSpinner == null) {
                locationSpinner = new Spinner(this);
                locationSpinner.setVisibility(View.INVISIBLE);

                ArrayList<String> strings = new ArrayList<String>();
                strings.add(getString(R.string.common_cancel_upper));
                for (int i = 0; i < locations.size(); i++) {
                    strings.add(locations.get(i).getTitle());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_item, strings);

                locationSpinner.setAdapter(adapter);
                locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> av, View view, int i, long l) {
                        if (i != 0) {
                            try {
                                dstLatitude = locations.get(i - 1).getLatitude();
                                dstLongitude = locations.get(i - 1).getLongitude();
                                handler.sendEmptyMessage(DRAW_ROUTE);
                            } catch (NullPointerException nPEx) {
                            }
                        }
                    }

                    public void onNothingSelected(AdapterView<?> av) {
                        Log.e("", "");
                    }
                });
                mapView.addView(locationSpinner);
            }

            locationSpinner.performClick();


        } catch (Exception e) {
        }
    }

    /**
     * Loads prepared map page to WebView.
     */
    private void showMap() {
        try {//ErrorLogging

            if (title != null) {
                if (title.length() > 0) {
                    setTitle(title);
                }
            }

            mapView.loadDataWithBaseURL("", htmlSource, "text/html", "utf-8", "");

        } catch (Exception e) {
        }
    }

    /**
     * Prepares route Google Map URL and starts page to show it.
     */
    private void startRoute() {
        try {//ErrorLogging

            String routeURL = "";

            StringBuilder sb = new StringBuilder();
            sb.append("http://maps.google.com/maps?saddr=");
            sb.append(srcLatitude / 1E6);
            sb.append(",");
            sb.append(srcLongitude / 1E6);
            sb.append("&daddr=");
            sb.append(dstLatitude / 1E6);
            sb.append(",");
            sb.append(dstLongitude / 1E6);
            sb.append("&ll=");
            sb.append((srcLatitude / 1E6 + dstLatitude / 1E6) / 2);
            sb.append(",");
            sb.append((srcLongitude / 1E6 + dstLongitude / 1E6) / 2);
            sb.append("&z=");

            int z = 0;
            int gr = 0;
            if (Math.abs(srcLatitude - dstLatitude)
                    > Math.abs(srcLongitude - dstLongitude)) {
                gr = Math.abs((int) (srcLatitude - dstLatitude));
            } else {
                gr = Math.abs((int) (srcLongitude - dstLongitude));
            }
            if (gr > (120 * 1E6)) {
                z = 1;
            } else if (gr > (60 * 1E6)) {
                z = 2;
            } else if (gr > (30 * 1E6)) {
                z = 3;
            } else if (gr > (15 * 1E6)) {
                z = 4;
            } else if (gr > (8 * 1E6)) {
                z = 5;
            } else if (gr > (4 * 1E6)) {
                z = 6;
            } else if (gr > (2 * 1E6)) {
                z = 7;
            } else if (gr > (1 * 1E6)) {
                z = 8;
            } else if (gr > (0.5 * 1E6)) {
                z = 9;
            } else {
                z = 10;
            }

            sb.append(z);

            routeURL = sb.toString();

            Intent intent = new Intent(this, ContactsMapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("url", routeURL);
            startActivity(intent);

        } catch (Exception e) {
        }
    }

    private void showProgressDialog() {
        if (progressDialog != null) {
            if (!progressDialog.isShowing()) {
                progressDialog = ProgressDialog.show(this, "", getString(R.string.common_loading_upper));
            }
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void closeActivity() {
        hideProgressDialog();
        finish();
    }

    /**
     * This interface helps to redirect WebView to location web page URL.
     */
    public void onLocationChanged(Location arg0) {
        if (arg0 != null) {
            gpsLocation = new MapLocation(arg0.getLatitude(),
                    arg0.getLongitude());
        }
    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }

    public void onProviderEnabled(String arg0) {
    }

    public void onProviderDisabled(String arg0) {
    }


    private final class JavaScriptInterface {

        public JavaScriptInterface() {
        }

        /**
         * Redirects WebView to location web page URL.
         *
         * @param urlToGo   the location web page URL
         * @param pointName
         */
        @JavascriptInterface
        public void goToUrl(String urlToGo, String pointName) {
            ContactsMapActivity.this.urlToGo = urlToGo;
            handler.sendEmptyMessage(GO_TO_URL);
        }
    }
}
