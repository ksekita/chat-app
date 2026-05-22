package in.tech_camp.chat_app.factories;

import com.github.javafaker.Faker;

import in.tech_camp.chat_app.form.UserForm;

public class UserFormFactory {
  private static final Faker faker = new Faker();

  public static UserForm createUser(){
    UserForm userForm = new UserForm();

    userForm.setName(faker.name().username());
    userForm.setEmail(faker.internet().emailAddress());
    userForm.setPassword(faker.internet().password(6, 12));
    userForm.setPasswordConfirmation(userForm.getPassword());

    return userForm;
  }
}
