package com.shortener.url.repository;

import com.shortener.url.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {
  boolean existsAccountByPassword(String password);
}
