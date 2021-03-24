package com.shortener.url.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Table stores information about registered accounts */
@Entity
@NoArgsConstructor
@Data
public class Account {

  @Id private String accountId;
  private String password;

  public Account(String accountId, String password) {
    this.accountId = accountId;
    this.password = password;
  }

  public Account(String accountId) {
    this.accountId = accountId;
  }

  @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
  private List<Url> urls = new ArrayList<>();
}
