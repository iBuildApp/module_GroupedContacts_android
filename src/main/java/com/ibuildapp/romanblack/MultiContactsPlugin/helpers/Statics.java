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
package com.ibuildapp.romanblack.MultiContactsPlugin.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

/**
 * This class contains global module variables.
 */
public class Statics {

    public static int color1 = Color.parseColor("#ff0000"); //BackGround
    public static int color2 = Color.parseColor("#00ff00");
    public static int color3 = Color.parseColor("#0000ff");
    public static int color4 = Color.parseColor("#ffff00");
    public static int color5 = Color.parseColor("#ff00ff");
    public static boolean isLight = false;

    public static Bitmap appyColorFilterForResource(Context context, int resourceId,  int color, PorterDuff.Mode mode ){
        Bitmap immutable = BitmapFactory.decodeResource(context.getResources(), resourceId);
        final Bitmap mutable = immutable.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(mutable);
        Paint p = new Paint();
        p.setColorFilter(new PorterDuffColorFilter(color, mode));
        c.drawBitmap(mutable, 0.f, 0.f, p);
        return mutable;
    }
}
