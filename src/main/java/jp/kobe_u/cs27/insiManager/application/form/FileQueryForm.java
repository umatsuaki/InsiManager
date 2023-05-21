package jp.kobe_u.cs27.insiManager.application.form;

import lombok.*;


/**
 * ファイルを検索するフォーム
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileQueryForm {

    //ユーザID
    private String uid;
    //教科ID
    private Integer sid;
    //ジャンルID
    private Integer gid;
    //年度
    private Integer year;
}
