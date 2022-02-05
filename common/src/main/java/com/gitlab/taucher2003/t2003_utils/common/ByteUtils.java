package com.gitlab.taucher2003.t2003_utils.common;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class ByteUtils {

    private ByteUtils() {
    }

    /**
     * Convert a {@link UUID} to a byte array
     *
     * @param uuid the uuid to convert
     * @return the resulting byte array
     * @see #byteToUuid(byte[])
     */
    public static byte[] uuidToByte(UUID uuid) {
        return ByteBuffer.wrap(new byte[16])
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits())
                .array();
    }

    /**
     * Converts a byte array to a {@link UUID}
     *
     * @param bytes the byte array to convert
     * @return the resulting uuid
     * @see #uuidToByte(UUID)
     */
    public static UUID byteToUuid(byte[] bytes) {
        var byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

    /**
     * Converts a string into a byte array
     *
     * @param input the string to convert
     * @return the converted byte array
     * @see #binaryToString(byte[])
     */
    public static byte[] stringToBinary(String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Converts a byte array to a string
     *
     * @param input the byte array to convert
     * @return the resulting string
     * @see #stringToBinary(String)
     */
    public static String binaryToString(byte[] input) {
        return new String(input, StandardCharsets.UTF_8);
    }
}
