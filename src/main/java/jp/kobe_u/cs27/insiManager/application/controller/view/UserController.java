package jp.kobe_u.cs27.insiManager.application.controller.view;

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
import jp.kobe_u.cs27.insiManager.domain.entity.User;
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

  /**
   * ユーザIDを指定して必要な情報を取得し、ファイル検索ページに入る
   * 未登録のユーザIDが指定された場合、初期ページに移動する
   *
   * @param model
   * @param attributes
   * @param form  ユーザID
   * @return 体調入力ページ
   */
  @GetMapping("/user/enter")
  public String confirmUserExistence(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          UidForm form,
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
    return "redirect:/fileinformation";
  }

  
  /**
   * ユーザの情報を取得し、確認画面を表示する
   *
   * @param model
   * @param attributes
   * @param form  ユーザID
   * @return ユーザ情報確認ページ
   */
  @GetMapping("/user/information")
  public String searchUserInformation(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          UidForm form,
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
      return "redirect:/";
    }

    // ユーザ情報をModelに登録する
    model.addAttribute(
        "uid",
        uid);
    model.addAttribute(
        "nickname",
        user.getNickname());
    model.addAttribute(
        "email",
        user.getEmail());

    // ユーザ情報確認ページ
    return "information";
  }

  /**
   * ユーザを登録する
   *
   * @param model
   * @param attributes
   * @param form  UserForm
   * @return ユーザ登録確認ページ
   */
  @PostMapping("/user/register")
  public String registerUser(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          UserForm form,
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
      service.createUser(form);
    } catch (ValidationException e) {
      // ユーザが登録済みであった場合
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserAlreadyExistsError",
          true);

      // ユーザ登録ページ
      return "redirect:/user/signup";
    }

    // ユーザIDとニックネームをModelに登録する
    model.addAttribute(
        "uid",
        form.getUid());
    model.addAttribute(
        "nickname",
        form.getNickname());
    
    //FileQueryFormをModelに追加する(Thymeleaf上ではfileQueryForm)
    model.addAttribute(new FileQueryForm());

    // ユーザ登録確認ページ
    return "signup/confirm";
  }

  /**
   * ユーザ登録が可能か確認する
   * ユーザが登録済みであった場合、ユーザ登録ページに戻る
   *
   * @param model
   * @param attributes
   * @param form  UserForm
   * @return ユーザ登録確認ページ
   */
  @GetMapping("/user/register/confirm")
  public String confirmUserRegistration(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          UserForm form,
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

  /**
   * ユーザ情報を更新する
   *
   * @param model
   * @param attributes
   * @param form  UserForm
   * @return 体調記録ページ
   */
  @PostMapping("/user/update")
  public String updateUser(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          UserForm form,
      BindingResult bindingResult) {

    // フォームにバリデーション違反があった場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserFormError",
          true);

      // リダイレクト先の引数としてユーザIDを渡す
      attributes.addAttribute(
          "uid",
          form.getUid());

      // ユーザ情報取得メソッドにリダイレクトする
      return "redirect:/user/information";
    }

    // ユーザ情報を更新する
    try {
      service.updateUser(form);
    } catch (ValidationException e) {
      // ユーザが存在しない場合
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserDoesNotExistError",
          true);
      // 初期ページに戻る
      return "redirect:/";
    }

    // ユーザIDとニックネームをModelに追加する
    model.addAttribute(
        "uid",
        form.getUid());
    model.addAttribute(
        "nickname",
        form.getNickname());


    // ファイル検索ページ
    return "fileQuery";
  }

  /**
   * ユーザ情報更新が可能か確認する
   *
   * @param model
   * @param attributes
   * @param form  UserForm
   * @return ユーザ情報更新確認ページ
   */
  @GetMapping("/user/update/confirm")
  public String confirmUserUpdate(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          UserForm form,
      BindingResult bindingResult) {

    // フォームにバリデーション違反があった場合
    if (bindingResult.hasErrors()) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserFormError",
          true);

      // リダイレクト先の引数としてユーザIDを渡す
      attributes.addAttribute(
          "uid",
          form.getUid());

      // ユーザ情報取得メソッドにリダイレクトする
      return "redirect:/user/information";
    }

    // ユーザIDを変数に格納する
    final String uid = form.getUid();

    // ユーザが登録済みか確認する
    if (!service.existsUser(uid)) {
      // エラーフラグをオンにする
      attributes.addFlashAttribute(
          "isUserAlreadyExistsError",
          true);

      // リダイレクト先の引数としてユーザIDを渡す
      attributes.addAttribute(
          "uid",
          form.getUid());

      // ユーザ情報取得メソッドにリダイレクトする
      return "redirect:/user/information";
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

    // ユーザ情報更新確認ページ
    return "confirmUpdate";
  }

  /**
   * ユーザを削除する
   *
   * @param model
   * @param attributes
   * @param form ユーザID
   * @return 初期ページ
   */
  @PostMapping("/user/delete")
  public String deleteUser(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          UidForm form,
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

    // ユーザを削除する
    service.deleteUser(uid);

    // 初期ページ
    return "redirect:/";
  }

  /**
   * ユーザ削除が可能か確認する
   *
   * @param model
   * @param attributes
   * @param form  　ユーザID
   * @return ユーザ削除確認ページ
   */
  @GetMapping("/user/delete/confirm")
  public String confirmUserDelete(
      Model model,
      RedirectAttributes attributes,
      @ModelAttribute
      @Validated
          UidForm form,
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
      return "redirect:/";
    }

    // ユーザ情報をModelに追加する
    model.addAttribute(
        "uid",
        uid);
    model.addAttribute(
        "nickname",
        user.getNickname());
    model.addAttribute(
        "email",
        user.getEmail());

    // ユーザ削除確認ページ
    return "confirmDelete";

  }
}
