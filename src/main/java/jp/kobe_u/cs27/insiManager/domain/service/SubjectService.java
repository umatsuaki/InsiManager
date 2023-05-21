package jp.kobe_u.cs27.insiManager.domain.service;

import jp.kobe_u.cs27.insiManager.domain.repository.SubjectRepository;
import jp.kobe_u.cs27.insiManager.domain.repository.FileRepository;
import jp.kobe_u.cs27.insiManager.application.form.SubjectForm;
import jp.kobe_u.cs27.insiManager.configuration.exception.ValidationException;
import jp.kobe_u.cs27.insiManager.domain.entity.Subject;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static jp.kobe_u.cs27.insiManager.configuration.exception.ErrorCode.*;

import java.util.List;
/**
 * 教科に関する処理を提供するサービスクラス
 */
@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjects;
    private final FileRepository files;
    
    
    /**
     * 教科を登録する
     *
     * @param form SubjectForm
     * @return 登録した教科の情報
     */
    public Subject createUser(SubjectForm form) {
  
      // 教科IDを変数に格納する
      final int sid = form.getSid();
  
      // 教科が登録済みであった場合、例外を投げる
      if (subjects.existsBySid(sid)) {
        throw new ValidationException(
            SUBJECT_ALREADY_EXISTS,
            "create the subject",
            String.format(
                "subject %s already exists",
                sid));
      }
  
      // 教科をDBに登録し、登録した教科の情報を戻り値として返す
      return subjects.save(new Subject(
          sid,
          form.getSubjectName(),
          form.getSubjectDescription()));
    }
  
    /**
     * 指定した教科がDBに登録済みかどうか確認する
     *
     * @param sid 教科ID
     * @return 指定した教科が存在するかどうかの真偽値(存在する場合にtrue)
     */
    public boolean existsSubject(int sid) {
  
      // 指定した教科がDBに登録済みか確認し、結果を戻り値として返す
      return subjects.existsById(sid);
    }
  
    /**
     * 教科の情報を取得する
     *
     * @param sid 教科ID
     * @return 教科の情報
     */
    public Subject getUser(int sid) {
  
      // 教科をDB上で検索し、存在すれば戻り値として返し、存在しなければ例外を投げる
      return subjects
          .findById(sid)
          .orElseThrow(() -> new ValidationException(
              SUBJECT_DOES_NOT_EXIST,
              "get the subject",
              String.format(
                  "subject %s does not exist",
                  sid)));
  
    }
  
    /**
     * 教科の情報を更新する
     *
     * @param form SubjectForm
     * @return 更新した教科の情報
     */
    public Subject updateUser(SubjectForm form) {
  
      // 教科IDを変数に格納する
      final int sid = form.getSid();
  
      // 教科が存在しない場合、例外を投げる
      if (!subjects.existsBySid(sid)) {
        throw new ValidationException(
            SUBJECT_DOES_NOT_EXIST,
            "update the subject",
            String.format(
                "subject %s does not exist",
                sid));
      }
  
      // DB上の教科情報を更新し、新しい教科情報を戻り値として返す
      return subjects.save(new Subject(
          sid,
          form.getSubjectName(),
          form.getSubjectDescription()));
  
    }
  
    /**
     * 教科を削除する
     * 処理に失敗した場合、このメソッド中のDB操作はすべてロールバックされる
     *
     * @param sid 教科ID
     */
    @Transactional
    public void deleteSubject(int sid) {
  
      // ユーザが存在しない場合、例外を投げる
      if (!subjects.existsById(sid)) {
        throw new ValidationException(
            USER_DOES_NOT_EXIST,
            "delete the subject",
            String.format(
                "subject %s does not exist",
                sid));
      }

      //IDからEntity獲得
      Subject subject = subjects.getReferenceById(sid); 
  
      // 教科を削除する
      subjects.deleteById(sid);
      // 教科に紐づいたファイルを全て削除する
      files.deleteBySubject(subject);
  
    }

    /**
     * すべての教科を教科ID順に取得する
     * @return　すべての教科の情報
     */

     public List<Subject> getAllSubject() {
      return subjects.findAllByOrderBySidAsc();
  }
}
