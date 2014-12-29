package com.denyszaiats.myreactions;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class Helper{

    public int getShapeSize(int level, int size, Context context){
        int type = getTypeDisplay(context);

        if (type == 1) {
            if ((size - level * 4) >= 40) {
                size = size - level * 4;
            } else {
                size = 40;
            }
        }

        if (type == 2) {
            if ((size - level * 4) >= 50) {
                size = size - level * 4;
            } else {
                size = 50;
            }
        }

        if (type == 3) {
            if ((size - level * 5) >= 40) {
                size = size - level * 5;
            } else {
                size = 40;
            }
        }

        if (type == 4) {
            if ((size - level * 5) >= 70) {
                size = size - level * 5;
            } else {
                size = 70;
            }
        }

        return size;
    }

    public int getShapeStartSize(Context context){
        int size = 0;
        int type = getTypeDisplay(context);

        if (type == 1) {
           size = 80;
        }

        if (type == 2) {
           size = 100;
        }

        if (type == 3) {
            size = 120;
        }

        if (type == 4) {
            size = 200;
        }

        return size;
    }

    private int getTypeDisplay(Context context){
        int type = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();

        if ((width > 0 && width < 500)){
            type = 1;
        }

        if ((width >= 500 && width < 800)){
            type = 2;
        }

        if ((width >= 800 && width < 1200)){
            type = 3;
        }

        if ((width >= 1200)){
            type = 4;
        }

        return type;
    }

}
