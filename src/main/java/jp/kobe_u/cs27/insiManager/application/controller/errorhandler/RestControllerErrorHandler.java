package jp.kobe_u.cs27.insiManager.application.controller.errorhandler;

import jp.kobe_u.cs27.insiManager.configuration.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;
import static jp.kobe_u.cs27.insiManager.configuration.exception.ErrorCode.*;

@RestControllerAdvice("jp.kobe-u.cs27.insiManager.application.controller.rest")
public class RestControllerErrorHandler {
     /*
   * UserValidationExceptionをBAD_REQUESTとして処理する
   */
  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Response<Object> hanleBadRequestException(ValidationException ex) {

    return ResponseCreator.fail(
        ex.getCode(),
        ex,
        null);
  }

  /*
   * コントローラーのバリデーションに違反したエラーをBAD_REQUESTとして処理する
   */
  @ExceptionHandler({ConstraintViolationException.class,
      MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Response<Object> handleValidException(Exception ex) {

    return ResponseCreator.fail(
        CONTROLLER_VALIDATION_ERROR,
        ex,
        null);
  }

  /*
   * コントローラーで受け取れない不正なリクエストをBAD_REQUESTとして処理する
   */
  @ExceptionHandler({HttpMessageNotReadableException.class,
      HttpRequestMethodNotSupportedException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Response<Object> handleConrollerException(Exception ex) {

    return ResponseCreator.fail(
        CONTROLLER_REJECTED,
        ex,
        null);
  }

  /*
   * その他のエラーを重大な未定義エラーとみなしINTERNAL_SERVER_ERRORとして処理する
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Response<Object> handleOtherException(Exception ex) {

    return ResponseCreator.fail(
        OTHER_ERROR,
        ex,
        null);
  }
}
