package jp.kobe_u.cs27.insiManager.application.form;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

/**
 * ジャンル登録・更新フォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenreForm {

  // ジャンルID
  private int gid;

  // ジャンルの名前
  @NotBlank
  private String genreName;

  // ジャンルの説明
  private String genreDescription;
}

