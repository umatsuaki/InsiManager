package jp.kobe_u.cs27.insiManager.domain.repository;

import jp.kobe_u.cs27.insiManager.domain.entity.Genre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
/*
 * ジャンルのリポジトリ
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre,Integer> {
    /**
     * すべてのジャンルをID順に取得
     * 
     * @param gid ジャンルID
     * 
     * @return すべてのジャンル
     */
    List<Genre> findAllByOrderByGidAsc();

    /**
     * ジャンルIDに対応するジャンルがあるか
     * 
     * @param gid
     * @return ジャンルがあるかどうか
     */
    boolean existsByGid(int gid);
}
