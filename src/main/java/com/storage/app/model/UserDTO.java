package com.storage.app.model;

import java.time.Instant;
import java.util.Set;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
  @NotBlank
  @Size(min = 1, max = 50)
  private String login;

  //  @NotNull
  @Size(min = 4, max = 100)
  private String password;

  @Size(max = 50)
  private String firstName;

  @Size(max = 50)
  private String lastName;

  @Email
  @Size(min = 5, max = 254)
  private String email;

  @Size(max = 256)
  private String imageUrl;

  private boolean activated = false;

  private Set<String> authorities;

  private String createdBy;

  private Instant createdDate;

  private String lastModifiedBy;

  private Instant lastModifiedDate;
}
