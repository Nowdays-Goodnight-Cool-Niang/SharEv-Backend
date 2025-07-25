package org.example.backend.profile.exception;

import org.example.backend.exception.BadRequestException;

public class ProfileNotFoundException extends BadRequestException {
    public ProfileNotFoundException() {
        super("프로필이 존재하지 않습니다.");
    }
}
