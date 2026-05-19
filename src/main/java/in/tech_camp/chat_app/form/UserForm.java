package in.tech_camp.chat_app.form;

import lombok.Data;

@Data
public class UserForm {
  private String name;
  private String email;
  private String password;
  private String passwordConfirmation;
}
