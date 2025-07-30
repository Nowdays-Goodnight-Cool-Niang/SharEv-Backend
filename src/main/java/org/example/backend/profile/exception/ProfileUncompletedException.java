package org.example.backend.profile.exception;

import org.example.backend.exception.BadRequestException;
import org.example.backend.exception.ExceptionCode;

public class ProfileUncompletedException extends BadRequestException {
    public ProfileUncompletedException() {
        super(ExceptionCode.PROFILE_UNCOMPLETED);
    }
}
