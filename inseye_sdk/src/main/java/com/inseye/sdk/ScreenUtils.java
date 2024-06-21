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
    private final DisplayMetrics displayMetrics;
    private final double verticalHalfAngleRangeRad;

    protected ScreenUtils(VisibleFov deviceFov) {
        this.verticalHalfAngleRangeRad = Math.toRadians(deviceFov.vertical / 2f);
        this.displayMetrics = Resources.getSystem().getDisplayMetrics();
    }

    /**
     * Converts angles (in radians) to screen space coordinates.
     *
     * @param gazeAngle2D the 2d angle in radians
     * @return a Vector2D object containing the screen space coordinates
     */
    public Vector2D angleToAbsoluteScreenSpace(Vector2D gazeAngle2D) {
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        double y = height / 2.0 * (1 - gazeAngle2D.getY() / verticalHalfAngleRangeRad);

        double aspectRatio = width / (double) height;
        double horizontalAngleRangeRad = verticalHalfAngleRangeRad * aspectRatio;

        double x = width / 2.0 * (1 + gazeAngle2D.getX() / horizontalAngleRangeRad);

        return new Vector2D(x, y);
    }

    /**
     * Converts screen space coordinates to view space coordinates relative to a given subview.
     *
     * @param screenSpace a Vector2D object containing the screen space coordinates
     * @param view the subview relative to which the coordinates are to be calculated
     * @return a Vector2D object containing the view space coordinates
     */
    public Vector2D screenSpaceToViewSpace(Vector2D screenSpace, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Vector2D(screenSpace.getX() - location[0], screenSpace.getY() - location[1]);
    }

    /**
     * Converts angles (in radians) to screen space coordinates and converts them to view space coordinates.
     * @param gazeAngle2D the 2d angle in radians
     * @param view the subview relative to which the coordinates are to be calculated
     * @return a Vector2D object containing the view space coordinates
     */
    public Vector2D angleToViewSpace(Vector2D gazeAngle2D, View view) {
        Vector2D screenSpace = angleToAbsoluteScreenSpace(gazeAngle2D);
        return screenSpaceToViewSpace(screenSpace, view);
    }
}
