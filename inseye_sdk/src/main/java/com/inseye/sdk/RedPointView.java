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

    public void setPoint(float angleX, float angleY, Context context) {
        Vector2D screenPoint = ScreenUtils.angleToScreenSpace(angleX, angleY, context);
        this.redPoint = ScreenUtils.screenSpaceToViewSpace(this, screenPoint);
        invalidate(); // Request to redraw the view
    }



}