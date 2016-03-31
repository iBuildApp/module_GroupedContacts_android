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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

public class ShadowDrawable extends BitmapDrawable {

    private static float BORDER_SIZE = 2;
    private static float SHADOW_SIZE = 8;
    private static float SHIFT = 2;

    private Bitmap bitmap;

    public ShadowDrawable(Resources resources, Bitmap bitmap) {
        super(resources, Bitmap.createScaledBitmap(bitmap,
                bitmap.getWidth() + Float.valueOf(BORDER_SIZE * 2).intValue() + Float.valueOf((SHADOW_SIZE = bitmap.getWidth() / 10) * 2).intValue(),
                bitmap.getHeight() + Float.valueOf(BORDER_SIZE * 2).intValue() + Float.valueOf((SHADOW_SIZE = bitmap.getHeight() / 10) * 2).intValue() + (int)SHIFT,
                false));

        this.bitmap = bitmap;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(new RectF(SHADOW_SIZE, SHADOW_SIZE, bitmap.getWidth() + BORDER_SIZE * 2 + SHADOW_SIZE, bitmap.getWidth() + BORDER_SIZE * 2 + SHADOW_SIZE), 12, 12, new Paint() {{
            setAntiAlias(true);
            setColor(Color.WHITE);
            setShadowLayer(SHADOW_SIZE, 0, SHIFT, Color.BLACK);
        }});
        canvas.drawBitmap(bitmap, BORDER_SIZE + SHADOW_SIZE, BORDER_SIZE + SHADOW_SIZE, null);
    }

}
