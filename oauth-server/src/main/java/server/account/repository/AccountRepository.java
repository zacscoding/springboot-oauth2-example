package server.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import server.account.entity.Account;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);
}
