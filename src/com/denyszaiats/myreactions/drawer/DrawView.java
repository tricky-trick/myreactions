package com.denyszaiats.myreactions.drawer;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

public abstract class DrawView extends View {

    public int backgroundColor;
    public int radius;
    public int sideSize;
    public int startX, startY;
    public Point startPoint;
    public Point endPoint;

    Paint paint = new Paint();

    public DrawView(Context context) {
        super(context);
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setSideSize(int sideSize) {
        this.sideSize = sideSize;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }
}