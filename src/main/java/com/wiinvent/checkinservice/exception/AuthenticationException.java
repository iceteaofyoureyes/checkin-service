package com.wiinvent.checkinservice.exception;

public class AuthenticationException extends AppException {
    public AuthenticationException() {
        super(ErrorCode.AUTHENTICATION_EXCEPTION);
        setMessage(ErrorCode.AUTHENTICATION_EXCEPTION.getMessage());
    }

    public AuthenticationException(String message) {
        super(ErrorCode.AUTHENTICATION_EXCEPTION);
        setMessage(message);
    }
}
