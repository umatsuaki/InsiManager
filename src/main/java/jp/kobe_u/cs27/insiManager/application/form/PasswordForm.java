package jp.kobe_u.cs27.insiManager.application.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordForm {
     // パスワード
  @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
  @NotNull
  private String password;
}
