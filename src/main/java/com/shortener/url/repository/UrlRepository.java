package com.shortener.url.repository;

import com.shortener.url.entity.Url;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url, String> {
  List<Url> findAllByAccount_AccountId(String accountId);
}
