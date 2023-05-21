package jp.kobe_u.cs27.insiManager.configuration.exception;

import static jp.kobe_u.cs27.insiManager.configuration.exception.CodeString.*;

/**
 * Response型でラップした戻り値を生成する静的メソッドを含むクラス
 */
public class ResponseCreator {

  /**
   * エラー発生時の戻り値を生成する
   *
   * @param ec エラーコード
   * @param ex 発生した例外
   * @param data 任意のデータ
   * @param <T> dataの型
   * @return エラー発生時の戻り値
   */
  public static <T> Response<T> fail(
      ErrorCode ec,
      Exception ex,
      T data) {

    return new Response<>(
        ERROR_PREFIX.getStr() + ec.getCode(),
        ex
            .getClass()
            .getName() + ":" + ex.getMessage(),
        data);
  }

  /**
   * 処理成功時の戻り値を生成する
   *
   * @param data 任意のデータ
   * @param <T> dataの型
   * @return 処理成功時の戻り値
   */
  public static <T> Response<T> succeed(T data) {

    return new Response<>(
        SUCCESS.getStr(),
        null,
        data);
  }

}
