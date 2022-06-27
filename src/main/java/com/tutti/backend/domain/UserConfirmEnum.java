package com.tutti.backend.domain;

public enum UserConfirmEnum {
    BEFORE_CONFIRM(false),
    OK_CONFIRM(true);

    boolean code;

    UserConfirmEnum(boolean code) {
        this.code = code;
    }
}