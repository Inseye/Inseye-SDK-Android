package com.inseye.test_sdk;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class RingsBackground extends FrameLayout {

    private Paint ringsPaint;
    private Point center = new Point(0,0);

    public RingsBackground(Context context) {
        super(context);
        init(null, 0);
    }

    public RingsBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RingsBackground(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        ringsPaint = new Paint();
        ringsPaint.setColor(Color.DKGRAY); // Black color
        ringsPaint.setStyle(Paint.Style.STROKE);
        ringsPaint.setStrokeWidth(5);

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        // Calculate screen center
        int screenCenterX = screenWidth / 2;
        int screenCenterY = screenHeight / 2;
        center = new Point(screenCenterX, screenCenterY);


        setWillNotDraw(false); // This is important to ensure that onDraw gets called
    }

    private void drawConcentricCircles(Canvas canvas, int numberOfCircles) {
        for (int i = 0; i < numberOfCircles; i++) {
            int radius = 100 * (i + 1);
            // draw from canvas center
            canvas.drawCircle(center.x, center.y, radius, ringsPaint);
        }
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        drawConcentricCircles(canvas, 13);
    }


}
