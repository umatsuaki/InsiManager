package jp.kobe_u.cs27.insiManager.application.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * ユーザIDフォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UidForm {

  // ユーザID
  @Pattern(regexp = "[0-9a-zA-Z_\\-]+")
  @NotNull
  private String uid;

}
