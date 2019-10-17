package com.storage.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends Audit {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotNull
  @Size(min = 1, max = 50)
  @Column(length = 50, unique = true, nullable = false)
  private String login;

  @JsonIgnore
  @NotNull
  @Size(min = 60, max = 60)
  @Column(name = "password_hash", length = 60, nullable = false)
  private String password;

  private String firstName;
  private String lastName;

  @Email
  @Size(min = 5, max = 254)
  @Column(length = 254, unique = true)
  private String email;

  @NotNull
  @Column(nullable = false)
  private boolean activated = false;

  @Size(max = 256)
  @Column(name = "image_url", length = 256)
  private String imageUrl;

  @JsonIgnore
  @ManyToMany
  @JoinTable(
      name = "user_authority",
      joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
  private Set<Authority> authorities = new HashSet<>();

  @JsonIgnore
  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = "user")
  private Set<FileDetails> fileDetails = new HashSet<>();
}