
# Inseye Android SDK Documentation

## Overview

The Inseye SDK provides tools for interacting with the Inseye eye tracking service on Android devices. This document provides an overview of the SDK's main classes and their functionalities.

## Table of Contents

- [Overview](#overview)
- [Installation](#installation)
- [Requirements](#requirements)
- [Usage](#usage)
  - [InseyeSDK](#inseyesdk)
  - [InseyeTracker](#inseyetracker)
- [License](#license)

## Installation

To use the Inseye SDK in your Android project, add the following dependency to your `build.gradle` file:

```gradle
dependencies {
    implementation 'com.inseye.sdk:inseye-sdk:1.0.0'
}
```

## Requirements

To function properly, the Inseye SDK requires the following components to be installed on the device:
- Inseye Android Service
    - [Inseye Android Service STANDARD](https://install.appcenter.ms/orgs/inseye/apps/inseye-service/distribution_groups/inseye%20public) - public version
    - [Inseye Android Service PRO](https://install.appcenter.ms/orgs/inseye/apps/inseye-service/distribution_groups/inseye%20internal) - internal and for privaledged users version
- Inseye Calibration - depend on target platform
  - [Inseye Calibration OpenXR Pico](https://install.appcenter.ms/orgs/inseye/apps/inseye-calibration-openxr-pico/distribution_groups/inseye%20public)
  - [Inseye Calibration OpenXR Oculus](https://install.appcenter.ms/orgs/inseye/apps/inseye-calibration-openxr-quest/distribution_groups/inseye%20public)
  - [Inseye Calibration FlatScreen](https://install.appcenter.ms/orgs/inseye/apps/inseye-calibration-flat-screen/distribution_groups/inseye%20public)

Please ensure these components are installed and running on the device.

## Usage

### InseyeSDK

`InseyeSDK` is the main entry point for interacting with the Inseye SDK.

#### Constructor

```java
public InseyeSDK(Context context)
```

`context` The application context.

#### Methods

##### `isServiceConnected()`

```java
public boolean isServiceConnected()
```

Returns `true` if the Inseye service is connected, `false` otherwise.

##### `getEyeTracker()`

```java
public CompletableFuture<InseyeTracker> getEyeTracker()
```

Asynchronously retrieves an instance of `InseyeTracker`. Returns a `CompletableFuture` that completes with an `InseyeTracker` instance when the service is connected, or completes exceptionally if an error occurs.

##### `disposeEyeTracker()`

```java
public void disposeEyeTracker()
```
Disposes IneyeTracker instance 

### InseyeTracker

`InseyeTracker` is the main class for interacting with the Inseye eye tracker.

#### Constructor
Its meant to be used only in `getEyeTracker()`
```java
protected InseyeTracker(ISharedService serviceInterface)
```

`serviceInterface` The interface for communicating with the Inseye service.

#### Methods

##### `getServiceVersion()`

```java
public Version getServiceVersion()
```

Returns the version of the Inseye service.

##### `getFirmwareVersion()`

```java
public Version getFirmwareVersion()
```

Returns the version of the eye tracker firmware.

##### `getTrackerAvailability()`

```java
public TrackerAvailability getTrackerAvailability()
```

Returns the current availability status of the eye tracker.

##### `subscribeToTrackerStatus(IEyetrackerEventListener eventListener)`

```java
public void subscribeToTrackerStatus(IEyetrackerEventListener eventListener)
```

Subscribes to eye tracker status events.

`eventListener` The listener to receive eye tracker status events.

##### `unsubscribeFromTrackerStatus()`

```java
public void unsubscribeFromTrackerStatus()
```

Unsubscribes from eye tracker status events.

##### `getDominantEye()`

```java
public Eye getDominantEye()
```

Returns the dominant eye of the user.

##### `getMostRecentGazeData()`

```java
public GazeData getMostRecentGazeData()
```

Returns the most recent gaze data, or `null` if no gaze data is available.

##### `subscribeToGazeData(GazeDataReader.IGazeData gazeData)`

```java
public void subscribeToGazeData(@NonNull GazeDataReader.IGazeData gazeData) throws InseyeTrackerException
```

Subscribes to gaze data updates.

`gazeData` The listener to receive gaze data updates.

Throws an `InseyeTrackerException` if an error occurs while subscribing to gaze data.

##### `unsubscribeFromGazeData()`

```java
public void unsubscribeFromGazeData()
```

Unsubscribes from gaze data updates.

##### `abortCalibration()`

```java
public void abortCalibration()
```

Aborts the ongoing calibration procedure.

##### `startCalibration()`

```java
public CompletableFuture<ActionResult> startCalibration()
```

Starts the built-in calibration procedure. Returns a `CompletableFuture` that completes when the calibration procedure finishes. The result of the future indicates whether the calibration was successful.

## License

This project is licensed under [place for license]
