package jp.kobe_u.cs27.insiManager.application.controller.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import jp.kobe_u.cs27.insiManager.domain.entity.FileEntity;
import jp.kobe_u.cs27.insiManager.domain.entity.Genre;
import jp.kobe_u.cs27.insiManager.domain.entity.Subject;
import jp.kobe_u.cs27.insiManager.domain.repository.FileRepository;
import jp.kobe_u.cs27.insiManager.domain.service.FileService;
import jp.kobe_u.cs27.insiManager.domain.service.GenreService;
import jp.kobe_u.cs27.insiManager.domain.service.SubjectService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FileController {

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

    private final FileService fileService;
    private final FileRepository files;
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

        // ファイルをデータベースに保存する
        fileService.saveFile(form);
        return "redirect:/user/addfile";
    }

    /**
     * ファイルをアップロードしたユーザ、教科、ジャンル、年度、キーワードで検索する
     *
     * @param model
     * @param attributes
     * @param form       FileQueryForm
     * @return 検索結果
     */
    @GetMapping("/fileQuery/search")
    public String searchFile(
            Model model,
            RedirectAttributes attributes,
            @ModelAttribute FileQueryForm form,
            BindingResult bindingResult) {

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
            return "redirect:/fileQuery/search";
        }

        // 空文字をnullに変換
        if (form.getUid() == "") {
            form.setUid(null);
        }

        // ファイルを検索し、結果をModelに格納する
        // PostQueryFormをModelに追加する(Thymeleaf上ではhealthQueryForm)
        model.addAttribute("fileQueryResult", fileService.query(form).getFilelist());

        // 検索結果
        return "fileQueryResult";
    }

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

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id) {
        // ダウンロード対象のファイルデータを取得
        Optional<FileEntity> file = files.findById(id);
        if (file.isPresent()) {
            fileService.deleteFile(id);
        }

        return "redirect:/user/deletefile";
    }

    @GetMapping("/filequery")
    public String showInformationPage(Model model, RedirectAttributes attributes,
            @ModelAttribute FileQueryForm form, BindingResult bindingResult) {

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

        model.addAttribute("resultSize", fileService.query(form).getFilelist().size());
        model.addAttribute("fileQueryResult", fileService.query(form).getFilelist());
        return "filequery";
    }

    @GetMapping("/user/deletefile")
    public String showDeletePage(Model model, RedirectAttributes attributes,
            @ModelAttribute FileQueryForm form, BindingResult bindingResult) {

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
        // 空文字をnullに変換
        if (form.getUid() == "") {
            form.setUid(null);
        }

        model.addAttribute("resultSize", fileService.query(form).getFilelist().size());
        model.addAttribute("fileQueryResult", fileService.query(form).getFilelist());
        return "delete";
    }

}
