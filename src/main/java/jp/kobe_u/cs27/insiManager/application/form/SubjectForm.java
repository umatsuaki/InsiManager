package jp.kobe_u.cs27.insiManager.application.form;

import lombok.*;

import jakarta.validation.constraints.NotBlank;


/**
 * 教科登録・更新フォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectForm {

  // 教科ID
  private int sid;

  // 教科の名前
  @NotBlank
  private String subjectName;

  // 教科の説明
  private String subjectDescription;

}

