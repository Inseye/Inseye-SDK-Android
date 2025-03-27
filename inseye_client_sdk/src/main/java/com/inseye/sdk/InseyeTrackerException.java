package com.inseye.sdk;

public class InseyeTrackerException extends Exception{
    public InseyeTrackerException(String cause) {
        super(cause);

    }

    public InseyeTrackerException(Exception e) {
        super(e);
    }
}
