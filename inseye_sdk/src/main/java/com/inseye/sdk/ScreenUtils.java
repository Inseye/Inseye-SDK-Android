package com.inseye.sdk;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Utility class for screen-related calculations.
 */
public class ScreenUtils {
    private static final double VERTICAL_HALF_ANGLE_RANGE_DEG = 38.4 / 2.;
    private static final double VERTICAL_HALF_ANGLE_RANGE_RAD = Math.toRadians(VERTICAL_HALF_ANGLE_RANGE_DEG);

    /**
     * Converts angles (in radians) to screen space coordinates.
     *
     * @param angleX the horizontal angle in radians
     * @param angleY the vertical angle in radians
     * @return a Vector2D object containing the screen space coordinates
     */
    public static Vector2D angleToScreenSpace(float angleX, float angleY) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        double y = height / 2.0 * (1 - angleY / VERTICAL_HALF_ANGLE_RANGE_RAD);

        double aspectRatio = width / (double) height;
        double horizontalAngleRangeRad = VERTICAL_HALF_ANGLE_RANGE_RAD * aspectRatio;

        double x = width / 2.0 * (1 + angleX / horizontalAngleRangeRad);

        return new Vector2D(x, y);
    }

    /**
     * Converts screen space coordinates to view space coordinates relative to a given subview.
     *
     * @param subview the subview relative to which the coordinates are to be calculated
     * @param screenSpace a Vector2D object containing the screen space coordinates
     * @return a Vector2D object containing the view space coordinates
     */
    public static Vector2D screenSpaceToViewSpace(View subview, Vector2D screenSpace) {
        int[] location = new int[2];
        subview.getLocationOnScreen(location);
        return new Vector2D(screenSpace.getX() - location[0], screenSpace.getY() - location[1]);
    }
}
