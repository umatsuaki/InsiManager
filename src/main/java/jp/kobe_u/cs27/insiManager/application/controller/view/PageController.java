package jp.kobe_u.cs27.insiManager.application.controller.view;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.kobe_u.cs27.insiManager.application.form.FileForm;
import jp.kobe_u.cs27.insiManager.application.form.FileQueryForm;
import jp.kobe_u.cs27.insiManager.application.form.PasswordForm;
import jp.kobe_u.cs27.insiManager.application.form.UserForm;
import jp.kobe_u.cs27.insiManager.domain.entity.FileEntity;
import jp.kobe_u.cs27.insiManager.domain.entity.Genre;
import jp.kobe_u.cs27.insiManager.domain.entity.Subject;
import jp.kobe_u.cs27.insiManager.domain.service.FileService;
import jp.kobe_u.cs27.insiManager.domain.service.GenreService;
import jp.kobe_u.cs27.insiManager.domain.service.SubjectService;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class PageController {

  private final FileService fileService;
  private final SubjectService subjectService;
  private final GenreService genreService;

  /**
   * ファイル検索ページを表示する
   * 
   * @param model
   * @param attributes
   * @param form
   * @param bindingResult
   * @return ファイル検索ページ
   */

  @GetMapping("/")
  public String showDeletePage(Model model, RedirectAttributes attributes,
      @ModelAttribute FileQueryForm form, BindingResult bindingResult, Pageable pageable) {

    model.addAttribute(new FileQueryForm());
    List<Genre> genreList = genreService.getAllGenre();
    model.addAttribute("genreList", genreList);
    List<Subject> subjectList = subjectService.getAllSubject();
    model.addAttribute("subjectList", subjectList);
   

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

    model.addAttribute("fileQueryResult", fileService.query(form, pageable).getFilePage());
    return "index";
  }

  /**
   * ユーザ登録ページを表示する
   * 
   * @param model
   * @return ユーザ登録ページ
   */

  @GetMapping("/user/signup")
  public String showUserRegistrationPage(Model model) {

    // UserFormをModelに追加する(Thymeleaf上ではuserForm)
    model.addAttribute(new UserForm());

    return "register";
  }

  /**
   * ファイル追加ページを表示する
   * 
   * @param model
   * @return ファイル追加ページ
   */

  @GetMapping("/user/addfile")
  public String showUploadFilePage(Model model) {

    model.addAttribute(new FileForm());
    List<Genre> genreList = genreService.getAllGenre();
    model.addAttribute("genreList", genreList);
    List<Subject> subjectList = subjectService.getAllSubject();
    model.addAttribute("subjectList", subjectList);
    List<FileEntity> fileList = fileService.get10File();
    model.addAttribute("fileList", fileList);
    return "upload";
  }

  /**
   * ファイル削除確認ページにパスワードを渡す
   * @param form
   * @param model
   * @param attributes
   * @param fid
   * @param bindingResult
   * @return
   */
  @PostMapping("delete/confirm/{fid}")
  public String submitPassword(PasswordForm form, Model model, RedirectAttributes attributes, @PathVariable long fid,
      BindingResult bindingResult) {
    String password = form.getPassword();

    if (bindingResult.hasErrors()) {
      return "redirect:getfid/" + fid;
    }
    if (!password.equals("insipassword")) {
      attributes.addFlashAttribute("isPasswordIncorrect", true);
      return "redirect:/getfid/" + fid;
    }
    return "redirect:/delete/" + fid;

  }

}
