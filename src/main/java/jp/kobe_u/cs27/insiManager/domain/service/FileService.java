package jp.kobe_u.cs27.insiManager.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.kobe_u.cs27.insiManager.application.controller.view.FileController;
import jp.kobe_u.cs27.insiManager.application.form.FileForm;
import jp.kobe_u.cs27.insiManager.application.form.FileQueryForm;
import jp.kobe_u.cs27.insiManager.configuration.exception.ValidationException;
import jp.kobe_u.cs27.insiManager.domain.dto.FileQueryResult;
import jp.kobe_u.cs27.insiManager.domain.entity.*;
import jp.kobe_u.cs27.insiManager.domain.repository.*;

import lombok.RequiredArgsConstructor;

import static jp.kobe_u.cs27.insiManager.configuration.exception.ErrorCode.*;

import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

@Service
@RequiredArgsConstructor
public class FileService {

    @Autowired
    private final GenreRepository genres;
    private final SubjectRepository subjects;
    private final UserRepository users;
    private final FileRepository files;

    private static Logger log = LoggerFactory.getLogger(FileController.class);
    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

    /**
     * ファイルを保存する
     * 
     * @param form
     * @return 記録したファイル
     */
    public void saveFile(FileForm form) {

        try {
            // IDからEntity取得
            User user = users.getReferenceById(form.getUid());
            Genre genre = genres.getReferenceById(form.getGid());
            Subject subject = subjects.getReferenceById(form.getSid());
            int year = form.getYear();
            String comment = form.getComment();

            

            // ファイルデータを取得
            MultipartFile multipartFile = form.getData();
            Integer.valueOf(year);
            String fileType = multipartFile.getContentType();
            long size = multipartFile.getSize();
            String fileSize = String.valueOf(size);
            String fileName;

            if (!comment.equals("")) {
                fileName = Integer.toString(year) + "年"
                        + subject.getSubjectName() + genre.getGenreName() + "---"
                        + comment;
            } else {
                fileName = Integer.toString(year) + "年"
                        + subject.getSubjectName() + genre.getGenreName();
            }

            log.info("FileName: " + fileName);
            log.info("Subject: " + subject.getSubjectName());
            log.info("Year: " + Integer.toString(year));
            log.info("ジャンル: " + genre.getGenreName());
            log.info("FileType: " + fileType);
            log.info("FileSize: " + fileSize);
            log.info("Comment: " + comment);

            InputStream inputStream = multipartFile.getInputStream();
            // InputStreamからバイト配列を取得
            byte[] fileBytes = inputStream.readAllBytes();
            // Blobオブジェクトに変換
            Blob blob = new SerialBlob(fileBytes);
            // InputStreamをクローズ
            inputStream.close();

            files.save(new FileEntity(
                    null,
                    user,
                    subject,
                    year,
                    genre,
                    fileName,
                    fileType,
                    fileSize,
                    comment,
                    false,
                    blob,
                    new Timestamp(System.currentTimeMillis())));
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception: " + e);
        }

        /*
         * // ユーザが登録されていない場合エラーを返す
         * if (!users.existsById(form.getUid())) {
         * throw new ValidationException(
         * USER_DOES_NOT_EXIST,
         * "record the health condition",
         * String.format(
         * "user %s does not exist",
         * form.getUid()));
         * }
         */
    }

    /**
     * 
     * @param fid
     * @return ダウンロードファイル
     * @throws SQLException
     */
    public ResponseEntity<Resource> loadFile(long fid) throws SQLException {
        if (files.existsById(fid)) {
            Optional<FileEntity> downloadFile = files.findById(fid);
            Blob downloadData = downloadFile.get().getData();
            byte[] bs;
            bs = downloadData.getBytes(1, (int) downloadData.length());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(downloadFile.get().getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + downloadFile.get().getFileName()
                                    + "\"")
                    .body(new ByteArrayResource(bs));

        }

        else {
            throw new ValidationException(
                    FILE_DOES_NOT_EXIST,
                    "delete the file",
                    String.format(
                            " file %s does not exist",
                            fid));
        }
    }

    /**
     * すべてのファイルを新しい順に取得
     */
    public List<FileEntity> getAllFile() {
        return files.findAllByOrderByRecordedOnDesc();
    }

    /**
     * uidが一致するすべてのファイルを新しい順に取得
     */
    public List<FileEntity> getAllUserFile(String uid) {
        return files.findAll(Specification
                .where(FileRepository.uidEquals(uid)),
                Sort.by(Sort.Direction.DESC, "recordedOn"));
    }

    /**
     * ファイルをユーザ、教科、年度、ジャンル、キーワードで検索する
     */

    public FileQueryResult query(FileQueryForm form) {
        // フォームの中身を変数に格納する
        final Integer sid = form.getSid();
        final Integer gid = form.getGid();
        final Integer year = form.getYear();
        final String uid = form.getUid();

        List<FileEntity> fileList = new ArrayList<>();

        Sort sort = Sort.by(
                Sort.Order.desc("year"),
                Sort.Order.asc("subject.sid"),
                Sort.Order.asc("genre.gid"));

        fileList = files.findAll(Specification
                .where(FileRepository.sidEquals(sid))
                .and(FileRepository.gidEquals(gid))
                .and(FileRepository.yearEquals(year))
                .and(FileRepository.uidEquals(uid)),
                sort);

        // 検索結果を返す
        return new FileQueryResult(
                uid,
                sid,
                year,
                gid,
                fileList);
    }

    /**
     * ファイルを教科で検索する
     */

     public FileQueryResult sidQuery(Integer sid) {
        // フォームの中身を変数に格納する
        

        List<FileEntity> fileList = new ArrayList<>();

        Sort sort = Sort.by(
                Sort.Order.desc("year"),
                Sort.Order.asc("subject.sid"),
                Sort.Order.asc("genre.gid"));

        fileList = files.findAll(Specification
                .where(FileRepository.sidEquals(sid))
                .and(FileRepository.gidEquals(null))
                .and(FileRepository.yearEquals(null))
                .and(FileRepository.uidEquals(null)),
                sort);

        // 検索結果を返す
        return new FileQueryResult(
                null,
                sid,
                null,
                null,
                fileList);
    }


    /**
     * ファイルを削除する
     * 処理に失敗した場合、このメソッド中のDB操作はすべてロールバックされる
     *
     * @param fid ファイルID
     */
    public void deleteFile(Long fid) {

        // ファイルが存在しない場合、例外を投げる
        if (!files.existsById(fid)) {
            throw new ValidationException(
                    FILE_DOES_NOT_EXIST,
                    "delete the file",
                    String.format(
                            " file %s does not exist",
                            fid));
        }
        // ファイルを削除する
        files.deleteById(fid);

    }

}
