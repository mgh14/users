package com.github.mgh14.users.repository;

import com.github.mgh14.users.repository.entity.CurrentToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentTokenRepository extends JpaRepository<CurrentToken, Long> {

  Optional<CurrentToken> findByJwt(String jwt);
}
