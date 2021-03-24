package com.shortener.url.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Table stores information about registered urls and related accountId */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Url {

  @Id private String shortUrl;
  private String originalUrl;
  private int redirectType;
  private Long statistic;

  @ManyToOne(fetch = FetchType.LAZY)
  private Account account;
}
