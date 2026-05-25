package in.tech_camp.chat_app.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import in.tech_camp.chat_app.ChatAppApplication;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.factories.UserFormFactory;
import in.tech_camp.chat_app.form.UserForm;
import in.tech_camp.chat_app.service.UserService;

@ActiveProfiles("test")
@SpringBootTest(classes = ChatAppApplication.class)
@AutoConfigureMockMvc
public class UserIntegrationTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;
  
  @Test
  public void ログインしていない状態でトップページにアクセスした場合サインインページに移動する() throws Exception {
    // トップページに移動し、再度ログインページにリダイレクトされることを確認する
    mockMvc.perform(get("/"))
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("http://localhost/users/login"));
  }

  @Test
  public void ログインに成功しトップページに遷移する() throws Exception {
    // 予め、ユーザーをDBに保存する
    UserForm userForm = UserFormFactory.createUser();
    UserEntity userEntity = new UserEntity();
    userEntity.setEmail(userForm.getEmail());
    userEntity.setName(userForm.getName());
    userEntity.setPassword(userForm.getPassword());
    userService.createUserWithEncryptedPassword(userEntity);

    // サインインページに遷移する
    mockMvc.perform(get("/users/login"))
    .andExpect(status().isOk())
    .andExpect(view().name("users/login"));
    
    // 保存したユーザーでログインする
     MvcResult loginResult = mockMvc.perform(post("/login")
                                      .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                      .param("email", userForm.getEmail())
                                      .param("password", userForm.getPassword())
                                      .with(csrf()))
                              .andExpect(status().isFound())
                              .andExpect(redirectedUrl("/"))
                              .andReturn();
    
    // トップページに再度アクセスし、ログインできていることを確認する
     mockMvc.perform(get("/").session((MockHttpSession)loginResult.getRequest().getSession()))
            .andExpect(status().isOk())
            .andExpect(view().name("rooms/index"));
  }

  @Test
  public void ログインに失敗し再びサインインページに戻ってくる() throws Exception {
    // サインインページに遷移する
    mockMvc.perform(get("/users/login"))
    .andExpect(status().isOk())
    .andExpect(view().name("users/login"));

    // 誤ったユーザーでログインするとエラーパスにリダイレクトされる
    mockMvc.perform(post("/login")
  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
.param("email","hoge@hoge.com")
.param("password", "hogefuga")
.with(csrf()))
.andExpect(status().isFound())
.andExpect(redirectedUrl("/login?error"));

    // エラーパスにリダイレクトされたとき、サインインのビューが表示される
mockMvc.perform(get("/login?error").param("errror",""))
.andExpect(status().isOk())
.andExpect(view().name("users/login"));
  }
}