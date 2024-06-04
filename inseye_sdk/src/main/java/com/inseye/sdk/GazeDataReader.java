package com.inseye.sdk;

import android.util.Log;

import androidx.annotation.Nullable;

import com.inseye.shared.communication.GazeData;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;

public class GazeDataReader extends Thread implements Closeable {

    private final String TAG = GazeDataReader.class.getName();
    private final DatagramSocket socket;
    private final ByteBuffer byteBuffer;
    private final LinkedBlockingQueue<GazeData> gazeBuffer;
    private final IGazeData gazeInterface;

    // Interface to provide a callback for when new gaze data is ready
    public interface IGazeData {
        void nextGazeDataReady(GazeData gazeData);
    }

    // Constructor to initialize GazeDataReader with a port and an optional callback
    public GazeDataReader(int port, @Nullable IGazeData gazeCallback) throws SocketException, UnknownHostException {
        gazeInterface = gazeCallback;

        // Initialize ByteBuffer with appropriate size and byte order
        byte[] array = new byte[GazeData.SERIALIZER.getSizeInBytes()];
        byteBuffer = ByteBuffer.wrap(array, 0, GazeData.SERIALIZER.getSizeInBytes()).order(ByteOrder.LITTLE_ENDIAN);

        // Bind the socket to the specified port and address
        InetAddress address = InetAddress.getByName("0.0.0.0");
        socket = new DatagramSocket(null);
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(address, port));
        Log.i(TAG, "port: " + socket.getLocalPort());

        // Initialize the buffer to hold incoming gaze data
        gazeBuffer = new LinkedBlockingQueue<>(100);
    }

    // Method to get the gaze buffer queue
    public LinkedBlockingQueue<GazeData> getGazeBuffer() {
        return gazeBuffer;
    }

    // Main method to run the thread and process incoming gaze data packets
    @Override
    public void run() {
        while (!isInterrupted()) {
            GazeData gazeData = new GazeData();
            DatagramPacket datagram = new DatagramPacket(byteBuffer.array(), byteBuffer.capacity());

            try {
                // Receive data packet and read it into the ByteBuffer
                socket.receive(datagram);
                byteBuffer.clear();
                GazeData.SERIALIZER.readFromBuffer(gazeData, byteBuffer);

                // Manage the gaze buffer, ensuring it does not exceed capacity
                if (gazeBuffer.remainingCapacity() == 0)
                    gazeBuffer.poll();
                gazeBuffer.offer(gazeData);

                // If a callback is provided, notify that new data is ready
                if (gazeInterface != null)
                    gazeInterface.nextGazeDataReady(gazeData);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    // Method to close the socket and interrupt the thread
    @Override
    public void close() {
        interrupt();
        if (socket != null) socket.close();
    }
}