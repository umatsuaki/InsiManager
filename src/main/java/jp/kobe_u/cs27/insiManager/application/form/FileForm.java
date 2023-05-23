package jp.kobe_u.cs27.insiManager.application.form;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;


/**
 * ファイルをアップロードするフォーム
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileForm {

    //ユーザID
    private String uid;
   
    // 教科ID
    private Integer sid;

    // ジャンルID
    private Integer gid;

    // 年度
    private Integer year;

    // ファイルデータ
    private MultipartFile data;

    private String comment;

    

}
