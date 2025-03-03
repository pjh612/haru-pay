package com.haru.auth.adapter.in.security;

import com.fasterxml.jackson.annotation.*;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberPrincipalMixin {
    @JsonCreator
    MemberPrincipalMixin(@JsonProperty("principal") Map<String, Object> principal) {
    }
}
