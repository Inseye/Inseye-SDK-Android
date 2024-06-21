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
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.Getter;

public class GazeDataReader extends Thread implements Closeable {

    private final String TAG = GazeDataReader.class.getName();
    private final DatagramSocket socket;
    private final ByteBuffer byteBuffer;
    @Getter
    private final LinkedBlockingQueue<GazeData> gazeBuffer;
    private final CopyOnWriteArrayList<IGazeData> gazeInterfaces;

    // Interface to provide a callback for when new gaze data is ready
    public interface IGazeData {
        void nextGazeDataReady(GazeData gazeData);
    }

    /**
     * Constructor for the GazeDataReader class
     * @param port The port to bind the socket to
     * @throws SocketException
     * @throws UnknownHostException
     */
    public GazeDataReader(int port) throws SocketException, UnknownHostException {
        gazeInterfaces = new CopyOnWriteArrayList<>();
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
        super.setName(GazeDataReader.class.getSimpleName());
    }

    /**
     * Add a listener to receive gaze data updates
     * @param gazeInterface The listener to add
     */
    public void addGazeListener(IGazeData gazeInterface) {
        gazeInterfaces.addIfAbsent(gazeInterface);
    }

    /**
     * Remove a listener to stop receiving gaze data updates
     * @param gazeInterface The listener to remove
     */
    public void removeGazeListener(IGazeData gazeInterface) {
        gazeInterfaces.remove(gazeInterface);
    }

    /**
     * Get the most recent gaze data packet
     * @return The most recent gaze data packet
     */
    public GazeData getMostRecentGazeData() {
        return gazeBuffer.peek();
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

                gazeInterfaces.forEach(x->x.nextGazeDataReady(gazeData));
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    /**
     * Close the socket and interrupt the thread
     */
    @Override
    public void close() {
        interrupt();
        if (socket != null) socket.close();
    }
}