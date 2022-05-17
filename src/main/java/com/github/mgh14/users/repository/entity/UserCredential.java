package com.github.mgh14.users.repository.entity;

import java.time.OffsetDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@RequiredArgsConstructor
@Entity
@Table(name = "user_credential", schema = "users")
public class UserCredential {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userCredentialId;
  private String email;
  private String password;

  @OneToOne(cascade = CascadeType.MERGE, mappedBy = "userCredential")
  private UserInfo userInfo;

  private String createdBy;
  private String modifiedBy;
  private OffsetDateTime createdAt;
  private OffsetDateTime modifiedAt;
}
