
[![](https://jitpack.io/v/Inseye/Inseye-SDK-Android.svg)](https://jitpack.io/#Inseye/Inseye-SDK-Android)
![](https://img.shields.io/badge/API-27%2B-brightgreen.svg?style=flat)

# Inseye Android SDK

## Overview

The Inseye SDK provides tools for interacting with the Inseye eye tracking service on Android devices. This document provides an overview of the SDK's main classes and their functionalities.

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
- Inseye Android Service - act as centrall hub fo
	- [Inseye Android Service STANDARD](https://install.appcenter.ms/orgs/inseye/apps/inseye-service/distribution_groups/inseye%20public) - public version with only necessary options exposed
	- [Inseye Android Service PRO](https://install.appcenter.ms/orgs/inseye/apps/inseye-service/distribution_groups/inseye%20internal) - for internal and privileged users. Requires access to be granted by Inseye 
- Inseye Calibration - depends on target platform
	- [Inseye Calibration OpenXR Pico](https://install.appcenter.ms/orgs/inseye/apps/inseye-calibration-openxr-pico/distribution_groups/inseye%20public)
  	- [Inseye Calibration OpenXR Oculus](https://install.appcenter.ms/orgs/inseye/apps/inseye-calibration-openxr-quest/distribution_groups/inseye%20public)
  	- [Inseye Calibration FlatScreen](https://install.appcenter.ms/orgs/inseye/apps/inseye-calibration-flat-screen/distribution_groups/inseye%20public)

Please ensure these components are installed and running on the device.

## Installation

Add it in your root `build.gradle` at the end of repositories:
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
Disposes InseyeTracker instance 

### InseyeTracker

`InseyeTracker` is the main class for interacting with the Inseye eye tracker.

#### Methods

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

##### `subscribeToGazeData(GazeDataReader.IGazeData gazeDataListener)`

```java
public void subscribeToGazeData(@NonNull GazeDataReader.IGazeData gazeDataListener) throws InseyeTrackerException
```

Subscribes to gaze data updates.

`gazeDataListener` The listener to receive gaze data updates.

Throws an `InseyeTrackerException` if an error occurs while subscribing to gaze data.

##### `unsubscribeFromGazeData()`

```java
public void unsubscribeFromGazeData()
```

Unsubscribes from gaze data updates.

##### `startCalibration()`

```java
public CompletableFuture<ActionResult> startCalibration()
```
Starts the built-in calibration procedure. Returns a `CompletableFuture` that completes when the calibration procedure finishes. The result of the future indicates whether the calibration was successful.

##### `getDominantEye()`

```java
public Eye getDominantEye()
```

Returns the dominant eye of the user.

##### `abortCalibration()`

```java
public void abortCalibration()
```

Aborts the ongoing calibration procedure.

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

### GazeData

`GazeData` represents the gaze data obtained from the eye tracker.

Data is representen in radian angles where (0,0) is located in screen center  

For screen space and view conversion form angle use methods from `ScreenUtils`

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
