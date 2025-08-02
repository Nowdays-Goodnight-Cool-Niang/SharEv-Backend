package org.example.backend.profile.exception;

import org.example.backend.exception.BadRequestException;
import org.example.backend.exception.ExceptionCode;

public class ProfileNotFoundException extends BadRequestException {
    public ProfileNotFoundException() {
        super(ExceptionCode.PROFILE_NOT_FOUND);
    }
}
