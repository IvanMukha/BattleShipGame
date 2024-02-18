package org.example.serverUtils;

import java.net.ServerSocket;
import java.io.IOException;

public class ServerUtils {

    public static boolean isPortAvailable(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}


