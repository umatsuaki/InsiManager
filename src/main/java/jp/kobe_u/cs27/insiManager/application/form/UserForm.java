package jp.kobe_u.cs27.insiManager.application.form;

import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * ユーザ登録・更新フォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserForm {

  // ユーザID
  @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
  @NotNull
  private String uid;

  // ニックネーム
  @NotBlank
  private String nickname;

  // メールアドレス
  @NotBlank
  @Email
  private String email;

}
