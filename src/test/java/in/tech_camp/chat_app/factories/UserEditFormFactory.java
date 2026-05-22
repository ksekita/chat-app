package in.tech_camp.chat_app.factories;

import com.github.javafaker.Faker;

import in.tech_camp.chat_app.form.UserEditForm;

public class UserEditFormFactory {
  private static final Faker faker = new Faker();

  public static UserEditForm createEditUser() {
    UserEditForm userEditForm = new UserEditForm();

    userEditForm.setName(faker.name().username());
    userEditForm.setEmail(faker.internet().emailAddress());

    return userEditForm;
  }
}
