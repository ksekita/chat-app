package in.tech_camp.chat_app.support;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import in.tech_camp.chat_app.form.UserForm;

public class LoginSupport {
  public static MockHttpSession login(MockMvc mockMvc, UserForm userForm) throws Exception {
    MvcResult loginResult = mockMvc.perform(post("/login")
                                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                            .param("email", userForm.getEmail())
                                            .param("password", userForm.getPassword())
                                            .with(csrf()))
                                    .andExpect(status().isFound())
                                    .andExpect(redirectedUrl("/"))
                                    .andReturn();
    return (MockHttpSession) loginResult.getRequest().getSession();
  }
}