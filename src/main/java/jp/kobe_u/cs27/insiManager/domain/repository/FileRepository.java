package jp.kobe_u.cs27.insiManager.domain.repository;


import jp.kobe_u.cs27.insiManager.domain.entity.FileEntity;
import jp.kobe_u.cs27.insiManager.domain.entity.Genre;
import jp.kobe_u.cs27.insiManager.domain.entity.Subject;
import jp.kobe_u.cs27.insiManager.domain.entity.User;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/*
 * ファイルのリポジトリ
 */
@Repository
public interface FileRepository 
    extends CrudRepository<FileEntity,Long>, JpaSpecificationExecutor<FileEntity> {
    
 /**
   * ファイルIDが一致するファイルを全て削除する
   *
   * @param fid ファイルID
   */
  void deleteByFid(Long fid);

    
 /**
   * 教科が一致するファイルを全て削除する
   *
   * @param subject 教科
   */
  void deleteBySubject(Subject subject);

    
 /**
   * ユーザが一致するファイルを全て削除する
   *
   * @param user ユーザ
   */
  void deleteByUser(User user);

  /**
   * ジャンルが一致するファイルを全て削除する
   *
   * @param genre ジャンル
   */
  void deleteByGenre(Genre genre);

  /**
    * すべての投稿を投稿日順に取得
    */
    List<FileEntity> findAllByOrderByRecordedOnDesc();

        


 /**
 * 検索要素に対するSpecification
 */

    public static Specification<FileEntity> uidEquals(String uid) {
    return uid == null ? null : (root, query, builder) -> builder.equal(root.get("user").get("uid"), uid);
}
    public static Specification<FileEntity> fidEquals(Long fid) {
        return fid == null ? null : (root, query, builder) -> builder.equal(root.get("file").get("fid"), fid);
    }
    
    public static Specification<FileEntity> sidEquals(Integer sid) {
        return sid == null ? null : (root, query, builder) -> builder.equal(root.get("subject").get("sid"), sid);
    }

    public static Specification<FileEntity> yearEquals(Integer year) {
        return year == null ? null : (root, query, builder) -> builder.equal(root.get("year"), year);
    }

    public static Specification<FileEntity> gidEquals(Integer gid) {
        return gid == null ? null : (root, query, builder) -> builder.equal(root.get("genre").get("gid"), gid);
    }

    public static Specification<FileEntity> keywordContains(String keyword) {
        return keyword == null ? null : (root, query, builder) -> builder.like(root.get("text"), keyword);
    }
 
}



