package server.account.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.account.repository.AccountRepository;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;

    @GetMapping("/accounts")
    public ResponseEntity getAllAccounts() {
        try {
            return ResponseEntity.ok(accountRepository.findAll());
        } catch (Exception e) {
            logger.warn("Exception occur while getting accounts.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
