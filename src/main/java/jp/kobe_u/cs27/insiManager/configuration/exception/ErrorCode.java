package jp.kobe_u.cs27.insiManager.configuration.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * エラーコード
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
  USER_DOES_NOT_EXIST(11),
  USER_ALREADY_EXISTS(12),
  SUBJECT_DOES_NOT_EXIST(13),
  SUBJECT_ALREADY_EXISTS(14),
  FILE_DOES_NOT_EXIST(15),
  FILE_ALREADY_EXISTS(16),
  GENRE_DOES_NOT_EXIST(17),
  GENRE_ALREADY_EXISTS(18),
  CONTROLLER_VALIDATION_ERROR(97),
  CONTROLLER_REJECTED(98),
  OTHER_ERROR(99);

  private final int code;
}
