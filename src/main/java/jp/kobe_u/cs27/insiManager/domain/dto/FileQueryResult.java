package jp.kobe_u.cs27.insiManager.domain.dto;

import jp.kobe_u.cs27.insiManager.domain.entity.*;
import lombok.Data;


import org.springframework.data.domain.Page;

@Data
public class FileQueryResult {
    
    // ユーザID
    private final String uid;

    // 教科ID
    private final Integer sid;

    // 年度
    private final Integer year;

    // ジャンルID
    private final Integer  gid;

    //検索結果のリスト
    private final Page<FileEntity> filePage;

}
