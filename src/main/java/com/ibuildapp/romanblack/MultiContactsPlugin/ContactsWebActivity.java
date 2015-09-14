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
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.appbuilder.sdk.android.AppBuilderModule;

/**
 * This activity represents location web page for contacts with web page type
 * or route URL for contacts with location type.
 */
public class ContactsWebActivity extends AppBuilderModule {

    private final int INITIALIZATION_FAILED = 0;
    private final int NEED_INTERNET_CONNECTION = 1;
    private boolean isOnline = false;
    private String link = "";
    private ProgressDialog progressDialog = null;
    private WebView webView = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INITIALIZATION_FAILED: {
                    Toast.makeText(ContactsWebActivity.this, R.string.alert_cannot_init, Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            hideProgressDialog();
                            finish();
                        }
                    }, 5000);
                }
                break;
                case NEED_INTERNET_CONNECTION: {
                    Toast.makeText(ContactsWebActivity.this, R.string.alert_no_internet,
                            Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            hideProgressDialog();
                            finish();
                        }
                    }, 5000);
                }
                break;
            }
        }
    };

    @Override
    public void create() {
        try {

            setContentView(R.layout.romanblack_multicontacts_web);
            setTitle(getString(R.string.multicontacts_webview_title));

            Intent currentIntent = getIntent();

            link = currentIntent.getStringExtra("link");

            if (link == null) {
                handler.sendEmptyMessage(INITIALIZATION_FAILED);
            }

            if (link.length() == 0) {
                handler.sendEmptyMessage(INITIALIZATION_FAILED);
            }

            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null && ni.isConnectedOrConnecting()) {
                isOnline = true;
            }

            if (!isOnline) {
                handler.sendEmptyMessage(NEED_INTERNET_CONNECTION);
            }

            webView = (WebView) findViewById(R.id.romanblack_multicontacts_web_webview);
            webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return false;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    showProgressDialog();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    hideProgressDialog();
                }
            });
            webView.loadUrl(link);

        } catch (Exception e) {
        }
    }

    private void showProgressDialog() {
        boolean showPD = false;
        if (progressDialog == null) {
            showPD = true;
        } else if (!progressDialog.isShowing()) {
            showPD = true;
        }

        if (showPD) {
            progressDialog = ProgressDialog.show(this, null, getString(R.string.common_loading_upper));
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface arg0) {
                    webView.stopLoading();
                }
            });
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
