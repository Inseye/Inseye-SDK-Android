package com.inseye.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.inseye.shared.communication.ISharedService;
import com.inseye.shared.utils.ServiceConnectionIntentFactory;

public class InseyeServiceBinder {
    private static final String TAG = InseyeServiceBinder.class.getSimpleName();

    private final Context context;
    private IServiceBindCallback inseyeServiceConnection;

    private boolean isConnected = false;

    // Method to check if the service is connected
    public boolean isConnected() { return isConnected; }

    // Callback interface to handle service connection events
    public interface IServiceBindCallback {
        void serviceConnected(ISharedService service);
        void serviceDisconnected();
        void serviceError(Exception e);
    }

    // Constructor to initialize the InseyeServiceBinder with a context
    public InseyeServiceBinder(Context context) {
        this.context = context;
    }

    // Method to bind the service with the provided callback
    public void bind(@NonNull IServiceBindCallback inseyeServiceConnection) {
        this.inseyeServiceConnection = inseyeServiceConnection;

        Intent connectionIntent = ServiceConnectionIntentFactory.CreateServiceConnectIntent(context);
        connectionIntent.putExtra(ServiceConnectionIntentFactory.META, "AndroidSDK " + BuildConfig.INSEYE_SDK_VERSION);

        // Attempt to bind the service
        boolean serviceExist = context.bindService(connectionIntent, internalConnection, Context.BIND_AUTO_CREATE);

        // If the service does not exist, trigger the error callback
        if(!serviceExist){
            inseyeServiceConnection.serviceError(new InseyeTrackerException("inseye service is not present in system"));
        }
    }

    // Internal ServiceConnection implementation to handle service connection events
    private final ServiceConnection internalConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // When the service is connected, get the ISharedService interface
            ISharedService inseyeServiceClient = ISharedService.Stub.asInterface(iBinder);
            isConnected = true;
            inseyeServiceConnection.serviceConnected(inseyeServiceClient);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // When the service is disconnected, update the connection status and trigger the callback
            isConnected = false;
            inseyeServiceConnection.serviceDisconnected();
        }
    };

    // Method to unbind the service
    public void unbind() {
        context.unbindService(internalConnection);
    }
}