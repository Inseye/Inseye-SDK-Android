
[![](https://jitpack.io/v/Inseye/Inseye-SDK-Android.svg)](https://jitpack.io/#Inseye/Inseye-SDK-Android)
![](https://img.shields.io/badge/API-27%2B-brightgreen.svg?style=flat)

# Inseye Android SDK

## Overview

The Inseye SDK provides tools for interacting with the Inseye eye tracking service on Android devices. This document provides en an overview of the SDK's main classes and their functionalities.

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
  - [InseyeSDK](#inseyesdk)
  - [InseyeTracker](#inseyetracker)
  - [GazeData](#gazedata)
  - [EyeTrackerEvent](#eyetrackerevent)
- [Example](#example)
- [License](#license)

## Requirements

To function properly, the Inseye SDK requires the following components to be installed on the device:
- Inseye Android Service
	- [Inseye Android Service](https://install.appcenter.ms/orgs/inseye/apps/inseye-service/distribution_groups/inseye%20public) - public version
	- [Inseye Android Service PRO]() - for internal and privileged users. Exposes additional settings. Requires access to be granted by Inseye 
- Inseye Calibration - depends on target platform
	- [Inseye Calibration OpenXR Pico](https://install.appcenter.ms/orgs/inseye/apps/inseye-calibration-openxr-pico/distribution_groups/inseye%20public)
  	- [Inseye Calibration OpenXR Oculus](https://install.appcenter.ms/orgs/inseye/apps/inseye-calibration-openxr-quest/distribution_groups/inseye%20public)
  	- [Inseye Calibration FlatScreen](https://install.appcenter.ms/orgs/inseye/apps/inseye-calibration-flat-screen/distribution_groups/inseye%20public)

Please ensure these components are installed and running on the device.


## Installation

Add JitPack repository to your root `build.gradle` at the end of repositories:
```gradle
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
		maven { url 'https://jitpack.io' }
	}
}
```

To use the Inseye SDK in your Android project, add the following dependency to your target module `build.gradle` file:

```gradle
dependencies {
    implementation 'com.github.Inseye:Inseye-SDK-Android:TAG'
}
```
Replace `TAG` with latest released version

## Usage

### InseyeSDK

`InseyeSDK` is the main entry point for interacting with the Inseye SDK.

#### Constructor

```java
public InseyeSDK(Context context)
```

`context` The application context.

#### Methods

##### *IsServiceConnected*

```java
public boolean isServiceConnected()
```

Returns `true` if the Inseye service is connected, `false` otherwise.

---

##### *GetEyeTracker*

```java
public CompletableFuture<InseyeTracker> getEyeTracker()
```

Asynchronously retrieves an instance of [InseyeTracker](#inseyetracker). Returns a `CompletableFuture` that completes with an [InseyeTracker](#inseyetracker) instance when the service is connected, or completes exceptionally if an error occurs.

---

##### *Dispose*

```java
public void dispose()
```
Disposes InseyeTracker instance 

---

### InseyeTracker

`InseyeTracker` is the main class for interacting with the Inseye eye tracker.

#### Methods

##### *GetTrackerAvailability*

```java
public TrackerAvailability getTrackerAvailability()
```

Returns the current availability status of the eye tracker.
###### TrackerAvailability
- `Available` - eye tracker is connected and ready to start new calibration or streaming gaze data
- `Disconnected` - eye tracker is disconnected from device
- `Calibrating` - eye tracker is in the process of calibration
- `Unavailable` - eye tracker is connected to device but not yet available for use
- `NotCalibrated` - eye tracker is not calibrated. Device must be calibrated before serving gaze data
- `Unknown` - the eyetracker is connected but unavailable for unknown reason. his flag should should only appear if client library is behind service library and new flags were added.

---

##### *SubscribeToTrackerStatus*

```java
public void subscribeToTrackerStatus(IEyetrackerEventListener eventListener)
```

Subscribes to eye tracker status events.

`eventListener` The listener to receive eye tracker status events. It contains callback with [TrackerAvailability](#trackeravailability)

---

##### *UnsubscribeFromTrackerStatus*

```java
public void unsubscribeFromTrackerStatus()
```

Unsubscribes from eye tracker status events.

---

##### *StartStreamingGazeData*

``` java
public void startStreamingGazeData() throws InseyeTrackerException
```

Starts streaming gaze data. Throws an InseyeTrackerException if gaze data streaming fails.

---

##### *StopStreamingGazeData*

``` java
public void stopStreamingGazeData()
```

Stops streaming gaze data. That means `IEyetrackerEventListener` stops receiving gaze updates and `getMostRecentGazeData()` returns `null`

---

##### *SubscribeToGazeData*

```java
public void subscribeToGazeData(@NonNull GazeDataReader.IGazeData gazeDataListener)
```

Subscribes to gaze data updates. `startStreamingGazeData()` must be called before this method.

`gazeDataListener` The listener to receive gaze data updates.

---

##### *UnsubscribeFromGazeData*

```java
public void unsubscribeFromGazeData()
```

Unsubscribes from gaze data updates.

---

##### *GetMostRecentGazeData*
```java
public GazeData getMostRecentGazeData()
```
Returns the most recent `GazeData`, or null if no gaze data is available. `startStreamingGazeData()` must be called before this method.

---

##### *StartCalibration*

```java
public CompletableFuture<ActionResult> startCalibration()
```
Starts the built-in calibration procedure. Returns a `CompletableFuture` that completes when the calibration procedure finishes. The result of the future indicates whether the calibration was successful.

---

##### *AbortCalibration*

```java
public void abortCalibration()
```

Aborts the ongoing calibration procedure.

---

##### *GetDominantEye*

```java
public Eye getDominantEye()
```

Returns the dominant eye of the user. This could be configured by user in `Inseye Service` settings ui

Possible eye values:
- `BOTH`
- `LEFT`
- `RIGHT`

---

##### *GetScreenUtils*

```java
public ScreenUtils getScreenUtils()
```

Returns the screen space converters for the Inseye tracker gaze data.

---

##### *GetServiceVersion*

```java
public Version getServiceVersion()
```

Returns the version of the Inseye service.

---

##### *GetCalibrationVersion*

```java
public Version getCalibrationVersion()
```

Returns the version of the eye tracker caliration app.

---

##### *GetFirmwareVersion*

```java
public Version getFirmwareVersion()
```

Returns the version of the eye tracker firmware.

### GazeData

`GazeData` represents the gaze data obtained from the eye tracker.

Data is representen in radian angles where (0,0) is located in screen center  

For screen space and view conversion form angle use methods from [`ScreenUtils`](https://github.com/Inseye/Inseye-SDK-Android/blob/main/inseye_sdk/src/main/java/com/inseye/sdk/ScreenUtils.java) in combination with [`GazeDataExtension`](https://github.com/Inseye/Inseye-SDK-Android/blob/main/inseye_sdk/src/main/java/com/inseye/sdk/GazeDataExtension.java)

#### Fields

- `timeMilli` - The timestamp in milliseconds.
- `left_x` - The x-coordinate of the left eye gaze angle.
- `left_y` - The y-coordinate of the left eye gaze angle.
- `right_x` - The x-coordinate of the right eye gaze angle.
- `right_y` - The y-coordinate of the right eye gaze angle.
- `event` - The `EyeTrackerEvent` associated with the gaze data.


### EyeTrackerEvent

`EyeTrackerEvent` is an enumeration representing various eye tracker events.

#### Enum Values

- `NONE` - No event.
- `BLINK_LEFT` - Blink detected in the left eye.
- `BLINK_RIGHT` - Blink detected in the right eye.
- `BLINK_BOTH` - Blink detected in both eyes.
- `SACCADE` - Rapid movement of the eye between fixation points.
- `HEADSET_MOUNT` - Headset has been mounted.
- `HEADSET_DISMOUNT` - Headset has been dismounted.
- `UNKNOWN` - Unknown event.

## Example
For a complete example of how to integrate and use the Inseye SDK, please visit [Inseye-Android-SDK-Demo](https://github.com/Inseye/Inseye-Android-SDK-Demo)

## License 
This repository is part of Inseye Software Development Kit.

By using content of this repository you agree to [SDK LICENSE](https://github.com/Inseye/Licenses/blob/master/SDKLicense.txt)
