package server.account.service;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.account.Account;
import server.account.AccountSerializer;
import server.account.entity.AccountEntity;
import server.account.repository.AccountRepository;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private ObjectMapper objectMapper;

    @PostConstruct
    private void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(AccountEntity.class, new AccountSerializer());
        objectMapper.registerModule(simpleModule);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountEntity account = accountRepository.findByEmail(username)
                                                 .orElseThrow(() -> new UsernameNotFoundException(username));

        return new Account(account);
    }

    /**
     * Returns a json given {@link AccountEntity}
     */
    public String serializeAccount(AccountEntity entity) throws IOException {
        if (entity == null) {
            return "null";
        }

        return objectMapper.writeValueAsString(entity);
    }
}
