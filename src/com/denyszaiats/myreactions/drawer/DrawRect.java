package com.denyszaiats.myreactions.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawRect extends DrawView {

    public DrawRect(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setColor(backgroundColor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        canvas.drawRect(startX, startY, startX + sideSize, startY + sideSize, paint);
    }

}
