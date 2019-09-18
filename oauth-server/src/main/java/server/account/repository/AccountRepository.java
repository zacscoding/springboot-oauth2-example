package server.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import server.account.entity.AccountEntity;

/**
 *
 * @GitHub : https://github.com/zacscoding
 */
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByEmail(String email);
}
