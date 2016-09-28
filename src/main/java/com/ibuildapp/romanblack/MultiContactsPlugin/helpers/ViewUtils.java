package com.ibuildapp.romanblack.MultiContactsPlugin.helpers;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

public abstract class ViewUtils {
    public static void setBackgroundLikeHeader(View view, int color){
        int backColor;
        if (color == Color.WHITE)
            backColor = Color.parseColor("#33000000");
        else if (color == Color.BLACK)
            backColor = Color.parseColor("#80FFFFFF");
        else backColor = Color.parseColor("#33FFFFFF");

        view.setBackgroundDrawable(new LayerDrawable(new Drawable[]{
                new ColorDrawable(color),
                new ColorDrawable(backColor )
        }));
    }
}
