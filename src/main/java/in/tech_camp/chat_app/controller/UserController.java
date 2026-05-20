package in.tech_camp.chat_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.form.LoginForm;
import in.tech_camp.chat_app.form.UserEditForm;
import in.tech_camp.chat_app.form.UserForm;
import in.tech_camp.chat_app.repository.UserRepository;
import in.tech_camp.chat_app.service.UserService;
import in.tech_camp.chat_app.validation.ValidationOrder;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  private final UserService userService;

  @GetMapping("/users/sign_up")
  public String showSignUp(Model model){
    model.addAttribute("userForm", new UserForm());
    return "users/signUp";
  }

  @PostMapping("/user")
  public String createUser(@ModelAttribute("userForm") @Validated(ValidationOrder.class) UserForm userForm, BindingResult result, Model model) {
    userForm.validatePasswordConfirmation(result);
    if(userRepository.existByEmail(userForm.getEmail())){
      result.rejectValue("email","null","Email already exists");
    }

    if(result.hasErrors()){
      List<String> errorMessages = result.getAllErrors().stream()
      .map(DefaultMessageSourceResolvable::getDefaultMessage)
      .collect(Collectors.toList());

      model.addAttribute("errorMessages", errorMessages);
      model.addAttribute("userForm", userForm);
      return "users/signUp";
    }
    UserEntity userEntity = new UserEntity();
    userEntity.setName(userForm.getName());
    userEntity.setEmail(userForm.getEmail());
    userEntity.setPassword(userForm.getPassword());

    try {
      userService.createUserWithEncryptedPassword(userEntity);
    } catch (Exception e) {
      System.out.println("エラー：" + e);
      model.addAttribute("userForm", userForm);
      return "users/signUp";
    }

    return "redirect:/";
  }

  @GetMapping("/users/login")
  public String loginForm(Model model){
    model.addAttribute("loginForm", new LoginForm());
    return "users/login";
  }

  @GetMapping("/login")
  public String login(@RequestParam(value = "error", required = false)String error, @ModelAttribute("loginForm") LoginForm loginForm, Model model){
    if(error != null){
      model.addAttribute("loginError", "メールアドレスかパスワードが間違っています。");
    }
    return "users/login";
  }

   @GetMapping("/users/{userId}/edit")
  public String editUserForm(@PathVariable("userId") Integer userId, Model model) {
    UserEntity user = userRepository.findById(userId);

    UserEditForm userForm = new UserEditForm();
    userForm.setId(user.getId());
    userForm.setName(user.getName());
    userForm.setEmail(user.getEmail());

    model.addAttribute("user", userForm);
    return "users/edit";
  }

 @PostMapping("/users/{userId}")
  public String updateUser(@PathVariable("userId") Integer userId, @ModelAttribute("user") @Validated(ValidationOrder.class) UserEditForm userEditForm, BindingResult result, Model model) {
    String newEmail = userEditForm.getEmail();
    if (userRepository.existsByEmailExcludingCurrent(newEmail, userId)) {
      result.rejectValue("email", "error.user", "Email already exists");
    }
    if (result.hasErrors()) {
      List<String> errorMessages = result.getAllErrors().stream()
                                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                    .collect(Collectors.toList());
      model.addAttribute("errorMessages", errorMessages);
      model.addAttribute("user", userEditForm);
      return "users/edit";
    }
    
    UserEntity user = userRepository.findById(userId);
    user.setName(userEditForm.getName());
    user.setEmail(userEditForm.getEmail());

    try {
      userRepository.update(user);
    } catch (Exception e) {
      System.out.println("エラー：" + e);
      model.addAttribute("user", userEditForm);
      return "users/edit";
    }

    return "redirect:/";
  }
}