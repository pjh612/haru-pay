package com.haru.auth.adapter.in.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class UuidMixin {

    @JsonCreator
    public static UUID fromString(String uuid) {
        return UUID.fromString(uuid);
    }

}
