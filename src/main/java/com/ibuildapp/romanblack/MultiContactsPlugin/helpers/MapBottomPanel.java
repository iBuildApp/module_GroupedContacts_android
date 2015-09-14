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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * This class represents LinearLayout with custom background.
 */
public class MapBottomPanel extends LinearLayout {

    private Paint innerPaint, borderPaint;

    public MapBottomPanel(Context context) {
        super(context);
        init();
    }

    public MapBottomPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Creates paints to draw custom background.
     */
    private void init() {
        innerPaint = new Paint();

        borderPaint = new Paint();
    }

    /**
     * Sets the custom background inner paint.
     * @param innerPaint 
     */
    public void setInnerPaint(Paint innerPaint) {
        this.innerPaint = innerPaint;
    }

    /**
     * Sets the custom background border paint.
     * @param borderPaint 
     */
    public void setBorderPaint(Paint borderPaint) {
        this.borderPaint = borderPaint;
    }

    /**
     * Draws LinearLayout with custom background.
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        RectF drawRect = new RectF();
        drawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());

        canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
        canvas.drawRoundRect(drawRect, 5, 5, borderPaint);

        super.dispatchDraw(canvas);
    }
}
