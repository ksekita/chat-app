package in.tech_camp.chat_app.form;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;

import in.tech_camp.chat_app.factories.UserFormFactory;
import in.tech_camp.chat_app.validation.ValidationPriority1;
import in.tech_camp.chat_app.validation.ValidationPriority2;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;


@ActiveProfiles("test")
@SpringBootTest
public class UserFormUnitTest {
  private UserForm userForm;

  private Validator validator;

  private BindingResult bindingResult;

  @BeforeEach
  public void setUp(){
    userForm = UserFormFactory.createUser();

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();

    bindingResult=Mockito.mock(BindingResult.class);
  }

  @Nested
  class ユーザーを作成できる場合{
    @Test
    public void nameとemailとpasswordとpasswordconfirmationが存在すれば登録できる(){
      //
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm,ValidationPriority1.class);
      assertEquals(0, violations.size());
    }
  }

  @Nested
  class ユーザーを作成できない場合{
    @Test
    public void nameが空では登録できない(){
      userForm.setName("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertEquals(1, violations.size());
      assertEquals("Name can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void emailが空では登録できない(){
      userForm.setEmail("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertEquals(1, violations.size());
      assertEquals("Email can't be blank", violations.iterator().next().getMessage());

    }

    @Test
    public void passwordが空では登録できない(){
      userForm.setPassword("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertEquals(1, violations.size());
      assertEquals("Password can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void emailはアットマークを含まないと登録できない(){
      userForm.setEmail("invalidEmail");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
      assertEquals(1, violations.size());
      assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが5文字以下では登録できない(){
      userForm.setPassword("aaaaa");
      userForm.setPasswordConfirmation("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
      assertEquals(1, violations.size());
      assertEquals("Password should be between 6 and 128 characters",violations.iterator().next().getMessage());

    }

    @Test
    public void passwordが129文字以上では登録できない(){
      String password ="a".repeat(129);
      userForm.setPassword(password);
      Set<ConstraintViolation<UserForm>>violations = validator.validate(userForm,ValidationPriority2.class);
      assertEquals(1,violations.size());
      assertEquals("Password should be between 6 and 128 characters", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordとpasswordConfirmationが不一致では登録できない(){
      //わざと違うパスワードを入れて不一致の状態を作り出す
       userForm.setPasswordConfirmation("differentPassword");
       //UserFormの中に作った独自のチェック関数を呼び出している
      userForm.validatePasswordConfirmation(bindingResult);
      //Mockitoの必殺技！verifyはちゃんと処理が実行されたかを確認するもの！
      verify(bindingResult).rejectValue("passwordConfirmation", "error.user", "Password confirmation doesn't match Password");
    }
  }
}
