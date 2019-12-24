package server.config;

import java.util.Arrays;
import java.util.HashSet;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.slf4j.Slf4j;
import server.account.entity.AccountEntity;
import server.account.entity.AccountRole;
import server.account.repository.AccountRepository;
import server.config.properties.ApplicationProperties;

@Slf4j
@Configuration
public class AppConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    // Insert dummy accounts
    // -
    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired
            private AccountRepository accountRepository;

            @Autowired
            private ApplicationProperties appProperties;

            @Autowired
            private PasswordEncoder passwordEncoder;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                if (!accountRepository.findByEmail(appProperties.getAdminUsername()).isPresent()) {
                    AccountEntity admin = AccountEntity.builder()
                                                       .email(appProperties.getAdminUsername())
                                                       .password(passwordEncoder.encode(appProperties
                                                                                                .getAdminPassword()))
                                                       .roles(new HashSet<>(Arrays.asList(
                                                               AccountRole.ADMIN, AccountRole.USER)))
                                                       .age(25)
                                                       .build();
                    admin = accountRepository.save(admin);
                    logger.info("Created a admin:{}", admin);
                } else {
                    logger.info("Skip to save a admin because already exist.");
                }

                if (!accountRepository.findByEmail(appProperties.getUserUsername()).isPresent()) {
                    AccountEntity user = AccountEntity.builder()
                                                      .email(appProperties.getUserUsername())
                                                      .password(passwordEncoder.encode(
                                                              appProperties.getUserPassword()))
                                                      .roles(new HashSet<>(Arrays.asList(AccountRole.USER)))
                                                      .age(20)
                                                      .build();

                    user = accountRepository.save(user);
                    logger.info("Created a user:{}", user);
                } else {
                    logger.info("Skip to save a user because already exist.");
                }
            }
        };
    }
}
