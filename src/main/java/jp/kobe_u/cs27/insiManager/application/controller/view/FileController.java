package jp.kobe_u.cs27.insiManager.application.controller.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jp.kobe_u.cs27.insiManager.application.form.FileForm;
import jp.kobe_u.cs27.insiManager.application.form.FileQueryForm;
import jp.kobe_u.cs27.insiManager.configuration.exception.ValidationException;
import jp.kobe_u.cs27.insiManager.domain.PageWrapper;
import jp.kobe_u.cs27.insiManager.domain.entity.FileEntity;
import jp.kobe_u.cs27.insiManager.domain.entity.Genre;
import jp.kobe_u.cs27.insiManager.domain.entity.Subject;
import jp.kobe_u.cs27.insiManager.domain.entity.User;
import jp.kobe_u.cs27.insiManager.domain.repository.FileRepository;
import jp.kobe_u.cs27.insiManager.domain.service.FileService;
import jp.kobe_u.cs27.insiManager.domain.service.GenreService;
import jp.kobe_u.cs27.insiManager.domain.service.SubjectService;
import jp.kobe_u.cs27.insiManager.domain.service.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FileController {

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

    private final FileService fileService;
    private final FileRepository files;
    private final UserService service;
    private final SubjectService subjectService;
    private final GenreService genreService;

    /**
     * ファイルをアップロードする
     * 
     * @param attributes
     * @param form       FileForm
     * @return アップロードページ
     * 
     * 
     */

    @PostMapping("/user/addfile/add")
    public String uploadFile(
            RedirectAttributes attributes,
            @ModelAttribute @Validated FileForm form,
            BindingResult bindingResult) {

        // ユーザIDを変数に格納する
        final String uid = form.getUid();

        // ユーザ情報をDBから取得する
        // ユーザが登録済みかどうかの確認も兼ねている
        User user;
        try {
            user = service.getUser(uid);
        } catch (ValidationException e) {
            // エラーフラグをオンにする
            attributes.addFlashAttribute(
                    "isUserDoesNotExistError",
                    true);
            // 初期ページに戻る
            return "redirect:/user/addfile";
        }

        // ファイルをデータベースに保存する
        fileService.saveFile(form);

        return "redirect:/user/addfile";
    }

    /**
     * ファイルをダウンロードする
     * 
     * @param id
     * @param response
     */
    @RequestMapping("/download")
    public void download(@RequestParam("id") long id, HttpServletResponse response) {
        // ダウンロード対象のファイルデータを取得
        Optional<FileEntity> file = files.findById(id);
        if (file.isPresent()) {
            FileEntity fileEntity = file.get();
            Blob fileData = fileEntity.getData();

            try {
                // ファイルダウンロードの設定を実施
                // ファイルの種類は指定しない
                response.setContentType("application/octet-stream");
                response.setHeader("Cache-Control", "private");
                response.setHeader("Pragma", "");
                /*
                 * response.setHeader("Content-Disposition"
                 * ,"attachment;filename=\"" + fileName + "\"");
                 */
                response.setHeader("Content-Disposition", "attachment; filename = fileName.pdf");

                try (OutputStream out = response.getOutputStream(); InputStream in = fileData.getBinaryStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.flush();
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    /**
     * ファイルをプレビューする
     * 
     * @param id
     * @param response
     * 
     */
    @RequestMapping("/preview/{fileName}")
    public void preview(@PathVariable("fileName") String fileName, @RequestParam("id") long id,
            HttpServletResponse response) {
        // ダウンロード対象のファイルデータを取得
        Optional<FileEntity> file = files.findById(id);
        if (file.isPresent()) {
            FileEntity fileEntity = file.get();
            Blob fileData = fileEntity.getData();

            try {
                // ファイルダウンロードの設定を実施
                // ファイルの種類は指定しない
                response.setContentType("application/pdf");
                response.setHeader("Cache-Control", "private");
                response.setHeader("Pragma", "");
                response.setHeader("Content-Disposition", "inline;");
                // ダウンロードファイルへ出力
                try (OutputStream out = response.getOutputStream(); InputStream in = fileData.getBinaryStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.flush();
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    /**
     * ファイルIDを受け取ってファイルを削除する
     * 
     * @param id
     * @return ファイル削除ページ
     */

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id) {
        // ダウンロード対象のファイルデータを取得
        Optional<FileEntity> file = files.findById(id);
        if (file.isPresent()) {
            fileService.deleteFile(id);
        }

        return "redirect:/user/deletefile";
    }

    /**
     * ファイルを検索する
     * 
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @return 検索ページ
     */

    @GetMapping("/filequery")
    public String showInformationPage(Model model, RedirectAttributes attributes,
            @ModelAttribute FileQueryForm form, BindingResult bindingResult, Pageable pageable) {

        model.addAttribute(new FileQueryForm());
        List<Genre> genreList = genreService.getAllGenre();
        model.addAttribute("genreList", genreList);
        List<Subject> subjectList = subjectService.getAllSubject();
        model.addAttribute("subjectList", subjectList);
        List<FileEntity> fileList = fileService.getAllFile();
        model.addAttribute("fileList", fileList);

        // フォームのバリデーション違反があった場合
        if (bindingResult.hasErrors()) {
            // ユーザIDに使用できない文字が含まれていた場合
            if (bindingResult.getFieldErrors().stream().anyMatch(it -> it.getField().equals("uid"))) {
                // エラーフラグをオンにする
                attributes.addFlashAttribute(
                        "isUidValidationError",
                        true);

                // 自分自身にリダイレクトする
                return "redirect:/filequery";
            }

            // 自分自身にリダイレクトする
            return "redirect:/filequery";
        }

        String uid = form.getUid();
        if (uid.equals("")) {
            uid = "null";
        }
        Integer sid = form.getSid();
        if (sid == null) {
            sid = -1;
        }
        Integer gid = form.getGid();
        if (gid == null) {
            gid = -1;
        }
        Integer year = form.getYear();
        if (year == null) {
            year = -1;
        }

        return "redirect:/filequery/" + uid + "/" + sid + "/" + gid + "/" + year;
    }

    @GetMapping("/filequery/{uid}/{sid}/{gid}/{year}")
    public String showFileInformationPage(Model model, RedirectAttributes attributes,
            @ModelAttribute FileQueryForm form, BindingResult bindingResult, Pageable pageable,
            @PathVariable("uid") String uid, @PathVariable("sid") Integer sid, @PathVariable("gid") Integer gid,
            @PathVariable("year") Integer year) {

        if (uid.equals("null")) {
            uid = null;
        }
        if (sid == -1) {
            sid = null;
        }
        if (gid == -1) {
            gid = null;
        }
        if (year == -1) {
            year = null;
        }

        model.addAttribute(new FileQueryForm());
        form.setUid(uid);
        form.setSid(sid);
        form.setGid(gid);
        form.setYear(year);

        List<Genre> genreList = genreService.getAllGenre();
        model.addAttribute("genreList", genreList);
        List<Subject> subjectList = subjectService.getAllSubject();
        model.addAttribute("subjectList", subjectList);
        List<FileEntity> fileList = fileService.getAllFile();
        model.addAttribute("fileList", fileList);

        // フォームのバリデーション違反があった場合
        if (bindingResult.hasErrors()) {
            // ユーザIDに使用できない文字が含まれていた場合
            if (bindingResult.getFieldErrors().stream().anyMatch(it -> it.getField().equals("uid"))) {
                // エラーフラグをオンにする
                attributes.addFlashAttribute(
                        "isUidValidationError",
                        true);

                // 自分自身にリダイレクトする
                return "redirect:/filequery";
            }

            // 自分自身にリダイレクトする
            return "redirect:/filequery";
        }

        Page<FileEntity> filePage = fileService.query(form, pageable).getFilePage();

        if (uid == "") {
            uid = "null";
        }
        if (sid == null) {
            sid = -1;
        }

        if (gid == null) {
            gid = -1;
        }

        if (year == null) {
            year = -1;
        }
        PageWrapper<FileEntity> page = new PageWrapper<FileEntity>(filePage,
                "/filequery/" + uid + "/" + sid + "/" + gid + "/" + year);
        model.addAttribute("page", page);
        model.addAttribute("fileQueryResult", filePage.getContent());
        model.addAttribute("url", "/filequery/" + uid + "/" + sid + "/" + gid + "/" + year);

        return "filequery";
    }

    /**
     * ファイルを削除するページを表示する
     * 
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @return ファイル削除ページ
     */
    @GetMapping("/user/deletefile")
    public String showDeletePage(Model model, RedirectAttributes attributes,
            @ModelAttribute FileQueryForm form, BindingResult bindingResult, Pageable pageable) {

        model.addAttribute(new FileQueryForm());
        List<Genre> genreList = genreService.getAllGenre();
        model.addAttribute("genreList", genreList);
        List<Subject> subjectList = subjectService.getAllSubject();
        model.addAttribute("subjectList", subjectList);
        List<FileEntity> fileList = fileService.getAllFile();
        model.addAttribute("fileList", fileList);

        // フォームのバリデーション違反があった場合
        if (bindingResult.hasErrors()) {
            // ユーザIDに使用できない文字が含まれていた場合
            if (bindingResult.getFieldErrors().stream().anyMatch(it -> it.getField().equals("uid"))) {
                // エラーフラグをオンにする
                attributes.addFlashAttribute(
                        "isUidValidationError",
                        true);

                // 自分自身にリダイレクトする
                return "redirect:/user/deletefile";
            }

            // ユーザIDのみの条件で自分自身にリダイレクトする
            return "redirect:/user/deletefile";
        }

        String uid;

        if (form.getUid() == null) {
            uid = "null";
        } else {
            uid = form.getUid();
            if (uid.equals("")) {
                uid = "null";
            }
        }
        Integer sid = form.getSid();
        if (sid == null) {
            sid = -1;
        }
        Integer gid = form.getGid();
        if (gid == null) {
            gid = -1;
        }
        Integer year = form.getYear();
        if (year == null) {
            year = -1;
        }

        return "redirect:/user/deletefile/" + uid + "/" + sid + "/" + gid + "/" + year;

    }

    /**
     * ファイルを削除するページを表示する
     * 
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @return ファイル削除ページ
     */
    @GetMapping("/user/deletefile/{uid}/{sid}/{gid}/{year}")
    public String showFileDeletePage(Model model, RedirectAttributes attributes,
            @ModelAttribute FileQueryForm form, BindingResult bindingResult, Pageable pageable,
            @PathVariable("uid") String uid, @PathVariable("sid") Integer sid, @PathVariable("gid") Integer gid,
            @PathVariable("year") Integer year) {

        if (uid.equals("null")) {
            uid = null;
        }
        if (sid == -1) {
            sid = null;
        }
        if (gid == -1) {
            gid = null;
        }
        if (year == -1) {
            year = null;
        }

        model.addAttribute(new FileQueryForm());
        form.setUid(uid);
        form.setSid(sid);
        form.setGid(gid);
        form.setYear(year);

        List<Genre> genreList = genreService.getAllGenre();
        model.addAttribute("genreList", genreList);
        List<Subject> subjectList = subjectService.getAllSubject();
        model.addAttribute("subjectList", subjectList);
        List<FileEntity> fileList = fileService.getAllFile();
        model.addAttribute("fileList", fileList);

        // フォームのバリデーション違反があった場合
        if (bindingResult.hasErrors()) {
            // ユーザIDに使用できない文字が含まれていた場合
            if (bindingResult.getFieldErrors().stream().anyMatch(it -> it.getField().equals("uid"))) {
                // エラーフラグをオンにする
                attributes.addFlashAttribute(
                        "isUidValidationError",
                        true);

                // 自分自身にリダイレクトする
                return "redirect:/user/deletefile";
            }

            // ユーザIDのみの条件で自分自身にリダイレクトする
            return "redirect:/user/deletefile";
        }
        if (uid == "" || uid == null) {
            uid = "null";
        }
        if (sid == null) {
            sid = -1;
        }

        if (gid == null) {
            gid = -1;
        }

        if (year == null) {
            year = -1;
        }
        Page<FileEntity> filePage = fileService.query(form, pageable).getFilePage();
        PageWrapper<FileEntity> page = new PageWrapper<FileEntity>(filePage,
                "/user/deletefile/" + uid + "/" + sid + "/" + gid + "/" + year);
        model.addAttribute("page", page);
        model.addAttribute("fileQueryResult", filePage.getContent());
        model.addAttribute("url", "/user/deletefile/" + uid + "/" + sid + "/" + gid + "/" + year);

        return "delete";
    }

    /**
     * 教科IDでファイルを検索する
     * 
     * @param model
     * @param attributes
     * @param form
     * @param bindingResult
     * @return 検索ページ
     */

    @GetMapping("/filequery/{sid}")
    public String showInformationGidPage(Model model, RedirectAttributes attributes,
            @ModelAttribute FileQueryForm form, BindingResult bindingResult, @PathVariable("sid") Integer sid,
            Pageable pageable) {

        model.addAttribute(new FileQueryForm());
        List<Genre> genreList = genreService.getAllGenre();
        model.addAttribute("genreList", genreList);
        List<Subject> subjectList = subjectService.getAllSubject();
        model.addAttribute("subjectList", subjectList);
        List<FileEntity> fileList = fileService.getAllFile();
        model.addAttribute("fileList", fileList);

        // フォームのバリデーション違反があった場合
        if (bindingResult.hasErrors()) {
            // ユーザIDに使用できない文字が含まれていた場合
            if (bindingResult.getFieldErrors().stream().anyMatch(it -> it.getField().equals("uid"))) {
                // エラーフラグをオンにする
                attributes.addFlashAttribute(
                        "isUidValidationError",
                        true);

                // 自分自身にリダイレクトする
                return "redirect:/fileQuery/search";
            }

            // ユーザIDのみの条件で自分自身にリダイレクトする
            return "redirect:/fileinformation";
        }
        // 空文字をnullに変換
        if (form.getUid() == "") {
            form.setUid(null);
        }

        Page<FileEntity> filePage = fileService.query(form, pageable).getFilePage();
        PageWrapper<FileEntity> page = new PageWrapper<FileEntity>(filePage, "/filequery/" + sid);
        model.addAttribute("page", page);
        model.addAttribute("fileQueryResult", filePage.getContent());
        model.addAttribute("url", "/filequery/" + sid);

        return "filequery";

    }

    /**
     * ファイルIDを入手してファイル削除確認ページに進む
     * 
     * @param fid
     * @param model
     * @param attributes
     * @return
     */
    @GetMapping("/getfid/{fid}")
    public String confirmUserRegistration(
            @PathVariable("fid") long fid,
            Model model,
            RedirectAttributes attributes) {

        // ファイルIDをModelに追加する
        model.addAttribute(
                "fid",
                fid);

        // ファイルの名前をModelに追加する
        model.addAttribute("fileName", fileService.getFile(fid).get().getFileName());
        model.addAttribute("user", fileService.getFile(fid).get().getUser().getUid());
        model.addAttribute("addedTime", fileService.getFile(fid).get().getRecordedOn());

        // ユーザ登録確認ページ
        return "confirmDelete";
    }

}
