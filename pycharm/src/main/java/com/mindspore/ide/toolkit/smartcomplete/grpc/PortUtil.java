package com.mindspore.ide.toolkit.smartcomplete.grpc;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class PortUtil {
    public static final int INVALID_PORT = 0;
    public static final int MIN_VALID_PORT = 5001;
    public static final int MAX_VALID_PORT = 65535;

    public PortUtil() {
    }

    public static boolean isPortIdle(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port);
             DatagramSocket datagramSocket = new DatagramSocket(port)) {
            serverSocket.setReuseAddress(true);
            datagramSocket.setReuseAddress(true);
            return true;
        } catch (IOException ioException) {
            return false;
        }
    }

    public static int findAnIdlePort(int port) {
        int validPort = port;
        if (port < MIN_VALID_PORT || port > MAX_VALID_PORT) {
            validPort = MIN_VALID_PORT;
        }
        int tryNums = 10;
        for (int index = 0; index < tryNums; ++index) {
            if (validPort + 2 * index > MAX_VALID_PORT) {
                return 0;
            }

            if (isPortIdle(validPort + 2 * index)) {
                return validPort + 2 * index;
            }
        }
        return INVALID_PORT;
    }
}