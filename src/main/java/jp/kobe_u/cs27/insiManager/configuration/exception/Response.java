package jp.kobe_u.cs27.insiManager.configuration.exception;

import lombok.Data;

/**
 * RESTful APIの戻り値
 *
 * @param <T> 実行結果の型
 */
@Data
public class Response<T> {

  // 成功・エラーコード
  private final String code;
  // メッセージ
  private final String message;
  // 実行結果
  private final T result;

}
