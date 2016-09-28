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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.appbuilder.sdk.android.AppBuilderModuleMainAppCompat;
import com.appbuilder.sdk.android.tools.NetworkUtils;
import com.ibuildapp.romanblack.MultiContactsPlugin.helpers.Statics;
import com.restfb.util.StringUtils;

/**
 * This activity represents location web page for contacts with web page type
 * or route URL for contacts with location type.
 */
public class ContactsWebActivity extends AppBuilderModuleMainAppCompat {

    private ProgressDialog progressDialog = null;
    private WebView webView = null;

    @Override
    public void create() {
        try {
            setContentView(R.layout.grouped_contacts_web);
            setTopBarTitle(getString(R.string.multicontacts_webview_title));

            setTopBarLeftButtonTextAndColor(getResources().getString(R.string.common_back_upper),
                    getResources().getColor(android.R.color.black), true, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });
            setTopBarTitleColor(getResources().getColor(android.R.color.black));
            setTopBarBackgroundColor(Statics.color1);

            Intent currentIntent = getIntent();
            String link = currentIntent.getStringExtra("link");

            if (StringUtils.isBlank(link)) {
                initializationFailed();
                return;
            }

            if (!NetworkUtils.isOnline(this))
                needInternetConnection();

            webView = (WebView) findViewById(R.id.grouped_contacts_web_web_view);
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
                public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ContactsWebActivity.this);
                    builder.setMessage(R.string.notification_error_ssl_cert_invalid);
                    builder.setPositiveButton(ContactsWebActivity.this.getResources().getString(R.string.gc_continue), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.proceed();
                        }
                    });
                    builder.setNegativeButton(ContactsWebActivity.this.getResources().getString(R.string.gc_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.cancel();
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    hideProgressDialog();
                }
            });
            webView.loadUrl(link);

        } catch (Exception e) {
            Log.e("GC", e.getMessage());
            e.printStackTrace();
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

    private void initializationFailed(){
        Toast.makeText(ContactsWebActivity.this, R.string.alert_cannot_init, Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                hideProgressDialog();
                finish();
            }
        }, 5000);
    }

    private void needInternetConnection(){
        Toast.makeText(ContactsWebActivity.this, R.string.alert_no_internet,
                Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                hideProgressDialog();
                finish();
            }
        }, 5000);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }
}
