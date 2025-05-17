package com.haru.common.util;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class UuidUtil {
    public static UUID uuidFromBase64(String base64String) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        if (decodedBytes.length != 16) {
            throw new IllegalArgumentException("Input byte array must be 16 bytes for a UUID");
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(decodedBytes);

        long mostSignificantBits = byteBuffer.getLong();
        long leastSignificantBits = byteBuffer.getLong();

        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
