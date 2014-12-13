package com.denyszaiats.myreactions.drawer;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;

public abstract class DrawView extends View{

    public int backgroundColor;
    public int radius;
    public int sideSize;
    public int startX, startY;

    Paint paint = new Paint();

    public DrawView(Context context) {
        super(context);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setSideSize(int sideSize) {
        this.sideSize = sideSize;
    }

}