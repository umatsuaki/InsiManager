package jp.kobe_u.cs27.insiManager.application.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.kobe_u.cs27.insiManager.application.form.FileQueryForm;
import jp.kobe_u.cs27.insiManager.application.form.UidForm;
import jp.kobe_u.cs27.insiManager.application.form.UserForm;
import jp.kobe_u.cs27.insiManager.configuration.exception.ValidationException;
import jp.kobe_u.cs27.insiManager.domain.entity.FileEntity;
import jp.kobe_u.cs27.insiManager.domain.entity.Genre;
import jp.kobe_u.cs27.insiManager.domain.entity.Subject;
import jp.kobe_u.cs27.insiManager.domain.service.FileService;
import jp.kobe_u.cs27.insiManager.domain.service.GenreService;
import jp.kobe_u.cs27.insiManager.domain.service.SubjectService;
import jp.kobe_u.cs27.insiManager.domain.service.UserService;
import lombok.RequiredArgsConstructor;

/**
 * ユーザ操作を行うコントローラークラス
 * Thymeleafに渡す値をModelに登録し、ページを表示する
 */
@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserService service;
  private final FileService fileService;
  private final SubjectService subjectService;
  private final GenreService genreService;

  /**
   * ユーザIDを指定して必要な情報を取得し、ファイル検索ページに入る
   * 未登録のユーザIDが指定された場合、初期ページに移動する
   *
   * @param model
   * @param attributes
   * @param form       ユーザID
   * @return 体調入力ページ
   */
  @GetMapping("/user/enter")
  public String confirmUserExistence(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute @Validated UidForm form,
      BindingResult bindingResult) {

    // ユーザIDに使用できない文字が含まれていた場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUidValidationError",
          true);

      // 初期画面に戻る
      return "redirect:/";
    }

    // ユーザIDを変数に格納する
    final String uid = form.getUid();

    String nickname;

    // ユーザIDからニックネームを取得する
    // ユーザが登録済みかどうかの確認も兼ねている
    try {
      nickname = service
          .getUser(uid)
          .getNickname();
    } catch (ValidationException e) { // ユーザIDが未登録であった場合
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserDoesNotExistError",
          true);
      // 初期ページに戻る
      return "redirect:/";
    }

    // ユーザIDとニックネームをModelに追加する
    attributes.addFlashAttribute(
        "uid",
        uid);
    attributes.addFlashAttribute(
        "nickname",
        nickname);

    // ファイル検索ページ
    return "redirect:/";
  }

  /**
   * ユーザを登録する
   *
   * @param model
   * @param attributes
   * @param form       UserForm
   * @return ユーザ登録確認ページ
   */
  @PostMapping("/user/register")
  public String registerUser(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute @Validated UserForm userform,
      @ModelAttribute FileQueryForm fileform,
      BindingResult bindingResult) {

    // フォームにバリデーション違反があった場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserFormError",
          true);

      // ユーザ登録ページ
      return "redirect:/user/signup";
    }

    // ユーザを登録する
    try {
      service.createUser(userform);
    } catch (ValidationException e) {
      // ユーザが登録済みであった場合
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserAlreadyExistsError",
          true);

      // ユーザ登録ページ
      return "redirect:/user/signup";
    }

    model.addAttribute(new FileQueryForm());
    List<Genre> genreList = genreService.getAllGenre();
    model.addAttribute("genreList", genreList);
    List<Subject> subjectList = subjectService.getAllSubject();
    model.addAttribute("subjectList", subjectList);
    List<FileEntity> fileList = fileService.getAllFile();
    model.addAttribute("fileList", fileList);
    model.addAttribute("resultSize", fileService.query(fileform).getFilelist().size());
    model.addAttribute("fileQueryResult", fileService.query(fileform).getFilelist());

    // ファイル検索ページ
    return "redirect:/";
  }

  /**
   * ユーザ登録が可能か確認する
   * ユーザが登録済みであった場合、ユーザ登録ページに戻る
   *
   * @param model
   * @param attributes
   * @param form       UserForm
   * @return ユーザ登録確認ページ
   */
  @GetMapping("/user/register/confirm")
  public String confirmUserRegistration(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute @Validated UserForm form,
      BindingResult bindingResult) {

    // フォームにバリデーション違反があった場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserFormError",
          true);

      // ユーザ登録ページ
      return "redirect:/user/signup";
    }

    // ユーザIDを変数に格納する
    final String uid = form.getUid();

    // ユーザが既に存在するか確認する
    if (service.existsUser(uid)) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserAlreadyExistsError",
          true);

      // ユーザ登録ページに戻る
      return "redirect:/user/signup";
    }

    // ユーザ情報をModelに追加する
    model.addAttribute(
        "uid",
        uid);
    model.addAttribute(
        "nickname",
        form.getNickname());
    model.addAttribute(
        "email",
        form.getEmail());

    // ユーザ登録確認ページ
    return "confirmRegistration";
  }

}
