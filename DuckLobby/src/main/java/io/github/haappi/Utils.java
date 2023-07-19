package io.github.haappi;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Utils {
    public static byte[] stringToByteArray(String message, String delim) {
        String[] msg = message.split(delim);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        for (String s : msg) {
            out.writeUTF(s);
        }

        return out.toByteArray();
    }

    public static byte[] stringToByteArray(String message) {
        return stringToByteArray(message, ";");
    }

}
