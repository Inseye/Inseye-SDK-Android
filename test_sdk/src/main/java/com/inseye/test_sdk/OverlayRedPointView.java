package com.inseye.test_sdk;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class OverlayRedPointView extends FrameLayout {

    private Vector2D redPoint = new Vector2D(0, 0);
    private Paint paint;

    public OverlayRedPointView(Context context) {
        super(context);
        init();
    }

    public OverlayRedPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverlayRedPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFFFF0000); // Red color
        paint.setStyle(Paint.Style.FILL);
        setWillNotDraw(false); // This is important to ensure that onDraw gets called
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawCircle((float) redPoint.getX(), (float) redPoint.getY(), 20, paint);
    }

    public void setPoint(Vector2D position) {
        this.redPoint = position;
        invalidate(); // Request to redraw the view
    }
}