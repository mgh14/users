package com.github.mgh14.users.repository.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@RequiredArgsConstructor
@Entity
@Table(name = "current_token", schema = "users")
public class CurrentToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long currentTokenId;

  @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_credential_user_credential_id", referencedColumnName = "userCredentialId")
  private UserCredential userCredential;

  private String jwt;
  private long expiry;
}
