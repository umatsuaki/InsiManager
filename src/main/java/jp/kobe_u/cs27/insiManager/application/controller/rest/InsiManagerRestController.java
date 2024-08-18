package jp.kobe_u.cs27.insiManager.application.controller.rest;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import jp.kobe_u.cs27.insiManager.configuration.exception.Response;
import jp.kobe_u.cs27.insiManager.configuration.exception.ResponseCreator;
import jp.kobe_u.cs27.insiManager.domain.entity.User;
import jp.kobe_u.cs27.insiManager.domain.service.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api")
public class InsiManagerRestController {
  private final UserService userService;

  /**
   * ユーザを検索する
   *
   * @param uid ユーザID
   * @return ユーザ情報
   */
  @GetMapping("/user")
  public Response<User> searchUser(
      @RequestParam("uid") @Pattern(regexp = "[0-9a-zA-Z_\\-]+") String uid) {

    // ユーザを検索し、結果をResponse型でラップして返す
    return ResponseCreator.succeed(userService.getUser(uid));

  }
}
