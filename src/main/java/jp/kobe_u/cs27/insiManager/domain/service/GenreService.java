package jp.kobe_u.cs27.insiManager.domain.service;


import jp.kobe_u.cs27.insiManager.domain.repository.GenreRepository;
import jp.kobe_u.cs27.insiManager.domain.repository.FileRepository;
import jp.kobe_u.cs27.insiManager.application.form.GenreForm;
import jp.kobe_u.cs27.insiManager.configuration.exception.ValidationException;
import jp.kobe_u.cs27.insiManager.domain.entity.Genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static jp.kobe_u.cs27.insiManager.configuration.exception.ErrorCode.*;

import java.util.List;
/**
 * ジャンルに関する処理を提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genres;
    private final FileRepository files;
    
    
    /**
     * ジャンルを登録する
     *
     * @param form GenreForm
     * @return 登録したジャンルの情報
     */
    public Genre createGenre(GenreForm form) {
  
      // ジャンルIDを変数に格納する
      int gid = form.getGid();
      
  
      // ジャンルが登録済みであった場合、例外を投げる
      if (genres.existsByGid(gid)) {
        throw new ValidationException(
            GENRE_ALREADY_EXISTS,
            "create the genre",
            String.format(
                "genre %s already exists",
                gid));
      }
  
      // ジャンルをDBに登録し、登録したジャンルの情報を戻り値として返す
      return genres.save(new Genre(
          gid,
          form.getGenreName(),
          form.getGenreDescription()));
    }
  
    /**
     * 指定したジャンルがDBに登録済みかどうか確認する
     *
     * @param gid ジャンルID
     * @return 指定したジャンルが存在するかどうかの真偽値(存在する場合にtrue)
     */
    public boolean existsGenre(int gid) {
  
      // 指定したジャンルがDBに登録済みか確認し、結果を戻り値として返す
      return genres.existsById(gid);
    }
  
    /**
     * ジャンルの情報を取得する
     *
     * @param gid ジャンルID
     * @return ジャンルの情報
     */
    public Genre getGenre(int gid) {
  
      // ジャンルをDB上で検索し、存在すれば戻り値として返し、存在しなければ例外を投げる
      return genres
          .findById(gid)
          .orElseThrow(() -> new ValidationException(
              GENRE_DOES_NOT_EXIST,
              "get the genre",
              String.format(
                  "genre %s does not exist",
                  gid)));
  
    }
  
    /**
     * ジャンルの情報を更新する
     *
     * @param form GenreForm
     * @return 更新したジャンルの情報
     */
    public Genre updateUser(GenreForm form) {
  
      // ジャンルIDを変数に格納する
      final int gid = form.getGid();
  
      // ジャンルが存在しない場合、例外を投げる
      if (!genres.existsByGid(gid)) {
        throw new ValidationException(
            GENRE_DOES_NOT_EXIST,
            "update the genre",
            String.format(
                "genre %s does not exist",
                gid));
      }
  
      // DB上のジャンル情報を更新し、新しいジャンル情報を戻り値として返す
      return genres.save(new Genre(
          gid,
          form.getGenreName(),
          form.getGenreDescription()));
  
    }
  
    /**
     * ジャンルを削除する
     * 処理に失敗した場合、このメソッド中のDB操作はすべてロールバックされる
     *
     * @param gid ジャンルID
     */
    @Transactional
    public void deleteGenre(int gid) {
  
      // ユーザが存在しない場合、例外を投げる
      if (!genres.existsById(gid)) {
        throw new ValidationException(
            GENRE_DOES_NOT_EXIST,
            "delete the genre",
            String.format(
                "genre %s does not exist",
                gid));
      }

      //IDからEntity獲得
      Genre genre = genres.getReferenceById(gid); 
  
      // ジャンルを削除する
      genres.deleteById(gid);
      // ジャンルに紐づいたファイルを全て削除する
      files.deleteByGenre(genre);
  
    }

    /**
     * すべてのジャンルをジャンルID順に取得する
     * @return　すべてのジャンルの情報
     */

    public List<Genre> getAllGenre() {
      return genres.findAllByOrderByGidAsc();
  }
}

