package sharev.account.dto.response;

import lombok.Getter;
import sharev.account.entity.Account;

@Getter
public class ResponseAccountInfo {
    private final Long id;
    private final String name;
    private final String email;

    public ResponseAccountInfo(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.email = account.getEmail();
    }
}
