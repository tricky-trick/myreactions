package com.denyszaiats.myreactions.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawCircle extends DrawView {

    public DrawCircle(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawCircle(radius, radius, radius, paint);
    }
}
