package com.haru.common.util;

import java.util.Base64;
import java.util.UUID;

public class UuidUtil {
    public static UUID uuidFromBase64(String base64String) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        return UUID.nameUUIDFromBytes(decodedBytes);
    }
}
