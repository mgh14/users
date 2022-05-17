package com.github.mgh14.users.repository.entity;

import java.time.OffsetDateTime;
import java.util.UUID;
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
@Table(name = "user_info", schema = "users")
public class UserInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userInfoId;

  @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_credential_user_credential_id", referencedColumnName = "userCredentialId")
  private UserCredential userCredential;

  private UUID externalId;
  private String firstName;
  private String lastName;

  private String createdBy;
  private String modifiedBy;
  private OffsetDateTime createdAt;
  private OffsetDateTime modifiedAt;
}
