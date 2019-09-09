package server.config;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;
import server.account.entity.Account;
import server.account.entity.AccountRole;
import server.account.repository.AccountRepository;
import server.config.properties.AppProperties;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@Configuration
public class AppConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired
            private AccountRepository accountRepository;

            @Autowired
            private AppProperties appProperties;

            @Autowired
            private PasswordEncoder passwordEncoder;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                if (!accountRepository.findByEmail(appProperties.getAdminUsername()).isPresent()) {
                    logger.info("Try to create a admin");
                    Account admin = Account.builder()
                                           .email(appProperties.getAdminUsername())
                                           .password(passwordEncoder.encode(appProperties.getAdminPassword()))
                                           .roles(new HashSet<>(Arrays.asList(
                                                   AccountRole.ADMIN, AccountRole.USER)))
                                           .build();
                    accountRepository.save(admin);
                } else {
                    logger.info("Skip to save admin account because already exist.");
                }

                if (!accountRepository.findByEmail(appProperties.getUserUsername()).isPresent()) {
                    logger.info("Try to create a user.");
                    Account admin = Account.builder()
                                           .email(appProperties.getUserUsername())
                                           .password(passwordEncoder.encode(appProperties.getUserPassword()))
                                           .roles(new HashSet<>(Arrays.asList(AccountRole.USER)))
                                           .build();
                    accountRepository.save(admin);
                } else {
                    logger.info("Skip to save user because already exist.");
                }
            }
        };
    }
}