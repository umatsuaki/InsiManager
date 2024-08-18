package jp.kobe_u.cs27.insiManager.domain.entity;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * ユーザエンティティ
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  // ユーザID
  @Id
  private String uid;

  // ニックネーム
  private String nickname;

  // メールアドレス
  private String email;

}

