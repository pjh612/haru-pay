package com.haru.userapi.application;


import com.haru.userapi.application.dto.QueryMemberResponse;

import java.util.UUID;

public interface QueryMemberUseCase {
    QueryMemberResponse queryByUsername(String username);

    QueryMemberResponse queryById(UUID id);
}
