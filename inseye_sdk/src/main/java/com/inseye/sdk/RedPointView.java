package com.inseye.sdk;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class RedPointView extends View {

    private Vector2D redPoint = new Vector2D(0,0);
    private Paint paint;

    private static final double VERTICAL_HALF_ANGLE_RANGE_DEG = 38.4 / 2.;
    private static final double VERTICAL_HALF_ANGLE_RANGE_RAD = Math.toRadians(VERTICAL_HALF_ANGLE_RANGE_DEG);

    public RedPointView(Context context) {
        super(context);
        init();
    }

    public RedPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RedPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFFFF0000); // Red color
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle((float) redPoint.getX(), (float) redPoint.getY(), 20, paint);
    }

    public void setPoint(float angleX, float angleY) {
        this.redPoint = angleToPoint(angleX, angleY);
        invalidate(); // Request to redraw the view
    }


    // its temporary converter. Later on api will provide coordinates in screen space along with angular position
    private Vector2D angleToPoint(float angleX, float angleY) {
        int width = getWidth();
        int height = getHeight();

        double y = height / 2.0 * (1 - angleY / VERTICAL_HALF_ANGLE_RANGE_RAD);

        double aspectRatio = width / (double) height;
        double horizontalAngleRangeRad = VERTICAL_HALF_ANGLE_RANGE_RAD * aspectRatio;

        double x = width / 2.0 * (1 + angleX / horizontalAngleRangeRad);

        return new Vector2D(x, y);
    }
}