package jp.kobe_u.cs27.insiManager.application.controller.view;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
  public String showLandingPage(){
    return "index";
  }

  @GetMapping("/user/signup")
  public String showUserRegistrationPage(Model model){

    // UserFormをModelに追加する(Thymeleaf上ではuserForm)
    model.addAttribute(new UserForm());

    return "register";
  }

  @GetMapping("/fileQuery")
  public String showConditionInputPage(Model model){

    // HealthConditionFormをModelに追加する(Thymeleaf上ではhealthConditionForm)
    model.addAttribute(new FileQueryForm());
    List<Genre> genreList = genreService.getAllGenre();
    model.addAttribute("genreList",genreList);
    List<Subject> subjectList = subjectService.getAllSubject();
    model.addAttribute("subjectList", subjectList);
    return "fileQuery";
  }

 

  @GetMapping("/user/addfile")
  public String showUploadFilePage(Model model){

      model.addAttribute(new FileForm());
      List<Genre> genreList = genreService.getAllGenre();
      model.addAttribute("genreList",genreList);
      List<Subject> subjectList = subjectService.getAllSubject();
      model.addAttribute("subjectList", subjectList);
      List<FileEntity> fileList = fileService.getAllFile();
      model.addAttribute("fileList", fileList);  
      return "upload";
  }

  
}
