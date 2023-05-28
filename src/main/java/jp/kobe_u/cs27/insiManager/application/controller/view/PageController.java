package jp.kobe_u.cs27.insiManager.application.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jp.kobe_u.cs27.insiManager.application.form.FileForm;
import jp.kobe_u.cs27.insiManager.application.form.FileQueryForm;
import jp.kobe_u.cs27.insiManager.application.form.UserForm;
import jp.kobe_u.cs27.insiManager.domain.service.*;
import jp.kobe_u.cs27.insiManager.domain.entity.*;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class PageController {

  private final FileService fileService;
  private final SubjectService subjectService;
  private final GenreService genreService;

  @GetMapping("/")
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
    return "index";
  }

  @GetMapping("/user/signup")
  public String showUserRegistrationPage(Model model) {

    // UserFormをModelに追加する(Thymeleaf上ではuserForm)
    model.addAttribute(new UserForm());

    return "register";
  }

  @GetMapping("/user/addfile")
  public String showUploadFilePage(Model model) {

    model.addAttribute(new FileForm());
    List<Genre> genreList = genreService.getAllGenre();
    model.addAttribute("genreList", genreList);
    List<Subject> subjectList = subjectService.getAllSubject();
    model.addAttribute("subjectList", subjectList);
    List<FileEntity> fileList = fileService.getAllFile();
    model.addAttribute("fileList", fileList);
    return "upload";
  }

}
