package server.account.rest;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.account.annotation.AuthPrincipal;
import server.account.dto.AccountDto;
import server.account.entity.AccountEntity;

/**
 * @GitHub : https://github.com/zacscoding
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final ModelMapper modelMapper;

    /**
     * Returns {@link AccountEntity} given authorized user or UNAUTHORIZED status if no auth.
     */
    @ApiOperation(value = "Getting current user's information")
    @GetMapping("/accounts/me")
    public ResponseEntity getAccount(@AuthPrincipal AccountEntity account) {
        if (account == null) {
            logger.info("/accounts/me is called with anonymous");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            logger.info("/accounts/me is called with {}", account.getEmail());
            return ResponseEntity.ok(modelMapper.map(account, AccountDto.class));
        } catch (Exception e) {
            logger.warn("Exception occur while serializing account", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
