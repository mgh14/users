package com.github.mgh14.users.repository;

import com.github.mgh14.users.repository.entity.UserCredential;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {

  Optional<UserCredential> findOneByEmail(String email);
}
