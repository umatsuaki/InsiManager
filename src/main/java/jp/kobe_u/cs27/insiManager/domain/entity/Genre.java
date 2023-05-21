package jp.kobe_u.cs27.insiManager.domain.entity;

import lombok.*;

import jakarta.persistence.*;

/**
 * ジャンルのエンティティ
 * 
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    // ジャンルID
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int gid;
 
  // ジャンルの名前
  private String genreName;
 
   // ジャンルの説明
  private String genreDescription;
}
