package com.haru.common.util;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UuidUtilTest {

    @Test
    void testUuidFromBase64_ValidBase64() {
        UUID originalUUID = UUID.randomUUID();
        String base64EncodedUUID = encodeToBase64(originalUUID);

        UUID restoredUUID = UuidUtil.uuidFromBase64(base64EncodedUUID);

        assertEquals(originalUUID, restoredUUID);
    }

    @Test
    void testUuidFromBase64_InvalidBase64() {
        String invalidBase64 = "invalid_base64_string";

        assertThrows(IllegalArgumentException.class, () -> UuidUtil.uuidFromBase64(invalidBase64));
    }

    @Test
    void testUuidFromBase64_InvalidLengthBase64() {
        String invalidLengthBase64 = Base64.getEncoder().encodeToString(new byte[8]);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> UuidUtil.uuidFromBase64(invalidLengthBase64));
        assertEquals("Input byte array must be 16 bytes for a UUID", exception.getMessage());
    }

    @Test
    void testUuidFromBase64_NullInput() {
        assertThrows(NullPointerException.class, () -> UuidUtil.uuidFromBase64(null));
    }

    private String encodeToBase64(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return Base64.getEncoder().encodeToString(buffer.array());
    }
}