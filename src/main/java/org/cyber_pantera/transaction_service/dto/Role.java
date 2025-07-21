package org.cyber_pantera.transaction_service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN,

    MANAGER,

    INSPECTOR,

    USER
}
