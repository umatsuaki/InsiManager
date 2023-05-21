package jp.kobe_u.cs27.insiManager.domain.repository;

import jp.kobe_u.cs27.insiManager.domain.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * ユーザのリポジトリ
 */

@Repository
public interface UserRepository extends JpaRepository<User,String>{
    /**
     * すべてのユーザをID順に取得
     * 
     * @param uid ユーザID
     * 
     * @return すべてのユーザ
     */
    List<User> findAllByOrderByUidAsc();

    
}
