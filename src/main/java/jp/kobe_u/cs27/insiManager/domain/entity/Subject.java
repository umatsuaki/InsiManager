package jp.kobe_u.cs27.insiManager.domain.entity;

import lombok.*;

import jakarta.persistence.*;

/**
 * 教科のエンティティ
 * 
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subject {
  // 教科ID
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int sid;

 // 教科の名前
 private String subjectName;

  // 教科の説明
 private String subjectDescription;

}
