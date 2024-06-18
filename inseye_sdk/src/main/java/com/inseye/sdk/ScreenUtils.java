package com.inseye.sdk;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;

import com.inseye.shared.communication.VisibleFov;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Utility class for screen-related calculations.
 */
public class ScreenUtils {
    private DisplayMetrics displayMetrics;
    private final double verticalHalfAngleRangeRad;

    public ScreenUtils(VisibleFov deviceFov) {
        this.verticalHalfAngleRangeRad = Math.toRadians(deviceFov.vertical / 2f);
        this.displayMetrics = Resources.getSystem().getDisplayMetrics();

    }

    /**
     * Converts angles (in radians) to screen space coordinates.
     *
     * @param angleX the horizontal angle in radians
     * @param angleY the vertical angle in radians
     * @return a Vector2D object containing the screen space coordinates
     */
    public Vector2D angleToScreenSpace(float angleX, float angleY) {
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        double y = height / 2.0 * (1 - angleY / verticalHalfAngleRangeRad);

        double aspectRatio = width / (double) height;
        double horizontalAngleRangeRad = verticalHalfAngleRangeRad * aspectRatio;

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
    public Vector2D screenSpaceToViewSpace(View subview, Vector2D screenSpace) {
        int[] location = new int[2];
        subview.getLocationOnScreen(location);
        return new Vector2D(screenSpace.getX() - location[0], screenSpace.getY() - location[1]);
    }
}
