package com.inseye.sdk;

import com.inseye.shared.communication.GazeData;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import lombok.experimental.ExtensionMethod;

public class GazeDataExtension {

    /**
     * Returns the combined gaze vector from both eyes
     * @param gazeData source gaze data
     * @return combined gaze vector in radians
     */
    public static Vector2D getGazeCombined(GazeData gazeData) {
        return new Vector2D((gazeData.left_x + gazeData.right_x) /2f,
                (gazeData.left_y + gazeData.right_y) /2f);
    }

    /**
     * Returns the gaze vector from the left eye
     * @param gazeData source gaze data
     * @return left eye gaze vector in radians
     */
    public static Vector2D getGazeLeftEye(GazeData gazeData) {
        return new Vector2D(gazeData.left_x, gazeData.left_y);
    }

    /**
     * Returns the gaze vector from the right eye
     * @param gazeData source gaze data
     * @return right eye gaze vector in radians
     */
    public static Vector2D getGazeRightEye(GazeData gazeData) {
        return new Vector2D(gazeData.right_x, gazeData.right_y);
    }
}
