package jp.kobe_u.cs27.insiManager.domain.entity;
import lombok.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

import java.sql.Blob;
import java.sql.Timestamp;


/**
 * ファイルエンティティ
 * ユーザそれぞれのマイフォルダを作る
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {

    //ファイルID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fid;
  
    // ユーザ
    @ManyToOne
    private User user;

    // 教科
    @ManyToOne
    private Subject subject;

    // 年度
    private int year;

    // 問題 or 解答 or ノート
    @ManyToOne
    private Genre genre;

    // ファイルの名前
    private String fileName;

    // ファイルの種類
    private String fileType;

    //　ファイルのサイズ
    private String fileSize;

    //　マイフォルダかどうか
    private boolean myfolder;

    @Lob
    // ファイルデータ
    private Blob data;

     // 追加日時
    //@Temporal(TemporalType.TIMESTAMP)
    private Timestamp recordedOn;
}
