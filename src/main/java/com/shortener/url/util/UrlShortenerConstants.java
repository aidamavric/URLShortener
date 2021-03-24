package com.shortener.url.util;

import lombok.experimental.UtilityClass;

/** Utility class containing common constants */
@UtilityClass
public class UrlShortenerConstants {

  public final String ACCOUNT_IS_SUCCESSFULLY_CREATED = "Account is successfully created.";
  public final String ACCOUNT_ID_ALREADY_EXISTS =
      "Unable to complete request because AccountId already exists.";

  public final String REGISTER_ACCOUNT_URL = "/account";
  public final String REGISTER_URL = "/register";
  public final String GET_STATISTIC_URL = "/statistic/{AccountId}";
  public final String HELP_URL = "/help";
  public static final String ACCOUNT_ID_MUST_NOT_BE_EMPTY_OR_NULL =
      "AccountId must not be empty or null.";
  public static final String URL_MUST_BE_VALID_URL_SYNTAX = "Url must be valid url syntax.";
  public static final String URL_MUST_NOT_BE_EMPTY_OR_NULL = "Url must not be empty or null.";
  public static final String SHOULD_HAVE_ON_OF_FOLLOWING_VALUES_301_OR_302 =
      "Redirect type should have on of following values: 301 or 302.";
}
