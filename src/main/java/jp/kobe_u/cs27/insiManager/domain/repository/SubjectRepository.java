package jp.kobe_u.cs27.insiManager.domain.repository;

import jp.kobe_u.cs27.insiManager.domain.entity.Subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
/*
 * 教科のリポジトリ
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject,Integer> {
    /**
     * すべての教科をID順に取得
     * 
     * @param sid 教科ID
     * 
     * @return すべての教科
     */
    List<Subject> findAllByOrderBySidAsc();

    /**
     * 教科IDに対応する教科があるか
     * 
     * @param sid
     * @return ジャンルがあるかどうか
     */
    boolean existsBySid(int sid);
}
