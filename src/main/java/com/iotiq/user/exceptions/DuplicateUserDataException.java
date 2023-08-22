package com.iotiq.user.exceptions;

import com.iotiq.commons.exceptions.ApplicationException;
import org.springframework.http.HttpStatus;

import java.util.List;

public class DuplicateUserDataException extends ApplicationException {
    public DuplicateUserDataException(String propertyName) {
        super(HttpStatus.CONFLICT, "duplicateUserDataException", List.of(propertyName));
    }
}
