package com.inseye.sdk;

import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.inseye.shared.communication.Eye;
import com.inseye.shared.communication.IBuiltInCalibrationCallback;
import com.inseye.shared.communication.ICalibrationCallback;
import com.inseye.shared.communication.IEyetrackerEventListener;
import com.inseye.shared.communication.IServiceBuiltInCalibrationCallback;
import com.inseye.shared.communication.IServiceCalibrationCallback;
import com.inseye.shared.communication.ISharedService;
import com.inseye.shared.communication.Version;
import com.inseye.shared.communication.TrackerAvailability;
import com.inseye.shared.communication.VisibleFov;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

import lombok.Getter;

/**
 * The main class for interacting with the Inseye eye tracker.
 */
public class InseyeTracker {
    private static final String TAG = InseyeTracker.class.getSimpleName();

    /**
     * Interface for listening to eye tracker status changes.
     */
    public interface IEyeTrackerStatusListener {
        void onTrackerAvailabilityChanged(TrackerAvailability availability);
    }
    /**
     *  Returns the version of the Inseye service.
     */
    @Getter
    private final Version serviceVersion = new Version();
    /**
     *  Returns the version of the eye tracker firmware.
     */
    @Getter
    private final Version firmwareVersion = new Version();
    /**
     *  Returns the version of the Inseye calibration.
     */
    @Getter
    private final Version calibrationVersion = new Version();
    /**
     * Returns the screen space utils for the Inseye tracker.
     */
    @Getter
    private final ScreenUtils screenUtils;

    private IServiceBuiltInCalibrationCallback calibrationAbortHandler;
    private final ISharedService serviceInterface;
    private GazeDataReader gazeDataReader;

    /**
     * Constructs a new InseyeTracker instance.
     *
     * @param serviceInterface The interface for communicating with the Inseye service.
     */
    protected InseyeTracker(ISharedService serviceInterface) {
        this.serviceInterface = serviceInterface;
        try {
            serviceInterface.getVersions(serviceVersion, firmwareVersion, calibrationVersion);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        screenUtils = new ScreenUtils(getVisibleFov());
    }

    /**
     * Returns the current availability status of the eye tracker.
     * Tracker is fully operational on status: Available
     *
     * @return The tracker availability.
     */
    public TrackerAvailability getTrackerAvailability() {
        try {
            return serviceInterface.getTrackerAvailability();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Subscribes to eye tracker status events.
     *
     * @param statusListener The listener to receive eye tracker status events.
     */
    public void subscribeToTrackerStatus(IEyeTrackerStatusListener statusListener) {
        try {
            serviceInterface.subscribeToEyetrackerEvents(new IEyetrackerEventListener.Stub() {
                @Override
                public void handleTrackerAvailabilityChanged(TrackerAvailability availability) throws RemoteException {
                    statusListener.onTrackerAvailabilityChanged(availability);
                }
            });
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Unsubscribes from eye tracker status events.
     */
    public void unsubscribeFromTrackerStatus() {
        try {
            serviceInterface.unsubscribeFromEyetrackerEvents();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the dominant eye of the user.
     *
     * @return The dominant eye.
     */
    public Eye getDominantEye() {
        try {
            return serviceInterface.getDominantEye();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Visible field of view on VR headset or view port size in AR device
     *
     * @return horizontal and vertical fov in degrees angle.
     */
   public VisibleFov getVisibleFov() {
       try {
           return serviceInterface.getVisibleFov();
       } catch (RemoteException e) {
           throw new RuntimeException(e);
       }
   }


    /**
     * Starts streaming gaze data.
     * @throws InseyeTrackerException if gaze data streaming fails.
     */
    public void startStreamingGazeData() throws InseyeTrackerException {
        try {
            int port = serviceInterface.startStreamingGazeData();
            if(port > 0) {
                Log.i(TAG, "port:" + port);
                if(gazeDataReader == null || gazeDataReader.isInterrupted()) {
                    gazeDataReader = new GazeDataReader(port);
                    gazeDataReader.start();
                }
            } else {
                Log.e(TAG, "gaze stream error port:" + port);
                throw new InseyeTrackerException("gaze stream error port:" + port);
            }
        } catch (RemoteException | UnknownHostException | SocketException e) {
            throw new InseyeTrackerException(e);

        }
    }

    /**
     * Stops streaming gaze data.
     */
    public void stopStreamingGazeData() {
        try {
            serviceInterface.stopStreamingGazeData();
            if (gazeDataReader != null) gazeDataReader.interrupt();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Subscribes to gaze data updates.
     * <p>
     * {@link #startStreamingGazeData()} must be called before calling this method.
     * @param gazeListener The listener to receive gaze data updates.
     */
    public void subscribeToGazeData(@NonNull GazeDataReader.IGazeData gazeListener)  {
        if(gazeDataReader != null) {
            gazeDataReader.addGazeListener(gazeListener);
        }

    }

    /**
     * Unsubscribes from gaze data updates.
     * @param gazeListener The listener to stop receiving gaze data updates.
     */
    public void unsubscribeFromGazeData(@NonNull GazeDataReader.IGazeData gazeListener) {
        if(gazeDataReader != null) {
            gazeDataReader.removeGazeListener(gazeListener);
        }
    }

        /**
     * Starts the built-in calibration procedure.
     *
     * @return A CompletableFuture that completes when the calibration procedure finishes.
     *         Throws an exception if the calibration procedure fails.
     */
    public CompletableFuture<Void> startCalibration() {
        CompletableFuture<Void> calibrationFuture = new CompletableFuture<>();
        try {
            calibrationAbortHandler = serviceInterface.startBuiltInCalibrationProcedure(new IBuiltInCalibrationCallback.Stub() {

                @Override
                public void finishCalibration(boolean success, String errorMessage) throws RemoteException {
                    if(success) {
                        calibrationFuture.complete(null);
                    } else {
                        calibrationFuture.completeExceptionally(new InseyeTrackerException(errorMessage));
                    }
                }

            });
        } catch (RemoteException e) {
            Log.e(TAG, "calibration remote exception: " + e);
            calibrationFuture.completeExceptionally(e);
        }

        return calibrationFuture;
    }

    /**
     * Aborts the ongoing calibration procedure.
     */
    public void abortCalibration() {
        try {
            if(calibrationAbortHandler != null)
                calibrationAbortHandler.abortCalibrationProcedure();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }




}
