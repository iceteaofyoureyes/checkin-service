package com.wiinvent.checkinservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    UNKNOWN_EXCEPTION(9999, "Unexpected system error occurs!", HttpStatus.INTERNAL_SERVER_ERROR),
    RESOURCE_NOT_FOUND(4004, "Resource not found", HttpStatus.NOT_FOUND),
    INVALID_PAYLOAD(4000, "Invalid request Payload. Check property 'data' for more information", HttpStatus.BAD_REQUEST),
    BUSINESS_RULE_EXCEPTION(4999, null, HttpStatus.BAD_REQUEST),
    FILE_VALIDATION_EXCEPTION(4009, "File is not valid", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_EXCEPTION(4001, "Authentication Failed. Error occurs.", HttpStatus.UNAUTHORIZED)
    ;


    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
