package server.account.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import server.account.entity.AccountRole;

@Data
public class AccountDto {

    private Long id;
    private String email;
    private String password;
    private int age;
    private Set<AccountRole> roles;

    @JsonIgnore
    public String getPassword() {
        return password;
    }
}
