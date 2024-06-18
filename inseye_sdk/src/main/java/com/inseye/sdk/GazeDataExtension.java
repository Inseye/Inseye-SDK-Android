package com.inseye.sdk;

import com.inseye.shared.communication.GazeData;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class GazeDataExtension {
    public static Vector2D getGazeMiddle(GazeData gazeData) {
        return new Vector2D((gazeData.left_x + gazeData.right_x) /2f,
                (gazeData.left_y + gazeData.right_y) /2f);
    }

    public static Vector2D getGazeLeftEye(GazeData gazeData) {
        return new Vector2D(gazeData.left_x, gazeData.left_y);
    }

}
