package com.wiinvent.checkinservice.exception;

public class FileValidationException extends AppException {
    public FileValidationException() {
        super(ErrorCode.FILE_VALIDATION_EXCEPTION);
        setMessage(ErrorCode.FILE_VALIDATION_EXCEPTION.getMessage());
    }

    public FileValidationException(String message) {
        super(ErrorCode.FILE_VALIDATION_EXCEPTION);
        setMessage(message);
    }
}
