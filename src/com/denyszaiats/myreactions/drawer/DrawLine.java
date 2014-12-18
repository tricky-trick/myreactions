package com.denyszaiats.myreactions.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

public class DrawLine extends DrawView {

    public DrawLine(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        Point a = startPoint;
        Point b = endPoint;

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(a.x, a.y);
        path.lineTo(b.x, b.y);
        path.close();

        canvas.drawPath(path, paint);
    }

}
