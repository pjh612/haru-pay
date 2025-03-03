package com.haru.userapi.domain.models;

import com.fasterxml.uuid.Generators;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Getter
@Setter
@Slf4j
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    private UUID id;
    private String username;
    private String password;
    private String name;
    private Gender gender;

    public static Member creatNew(String username, String password, String name, Gender gender) {
        return new Member(Generators.timeBasedEpochGenerator().generate(), username, password, name, gender);
    }
}
