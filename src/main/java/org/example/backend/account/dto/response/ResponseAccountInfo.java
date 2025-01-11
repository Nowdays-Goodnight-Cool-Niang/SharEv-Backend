package org.example.backend.account.dto.response;

import lombok.Getter;
import org.example.backend.account.entity.Account;

@Getter
public class ResponseAccountInfo {
    private final Long id;
    private final String name;
    private final String phone;
    private final int profileImageId;

    public ResponseAccountInfo(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.phone = account.getPhone();
        this.profileImageId = account.getProfileImageId();
    }
}
