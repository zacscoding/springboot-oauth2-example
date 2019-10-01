package server.account.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.account.annotation.CurrentUser;
import server.account.entity.AccountEntity;
import server.account.service.AccountService;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * Returns {@link AccountEntity} given authorized user or UNAUTHORIZED status if no auth.
     */
    @GetMapping("/accounts/me")
    public ResponseEntity getAccount(@CurrentUser AccountEntity account) {
        if (account == null) {
            logger.info("/accounts/me is called with anonymous");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            logger.info("/accounts/me is called with {}", account.getEmail());
            return ResponseEntity.ok(accountService.serializeAccount(account));
        } catch (Exception e) {
            logger.warn("Exception occur while serializing account", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
