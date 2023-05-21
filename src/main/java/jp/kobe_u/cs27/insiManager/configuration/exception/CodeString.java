package jp.kobe_u.cs27.insiManager.configuration.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RESTful APIの戻り値で用いる文字列
 */
@AllArgsConstructor
@Getter
public enum CodeString {
  SUCCESS("OK"),
  ERROR_PREFIX("E");

  private final String str;
}
