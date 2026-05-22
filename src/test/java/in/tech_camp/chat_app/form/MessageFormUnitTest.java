package in.tech_camp.chat_app.form;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;

import in.tech_camp.chat_app.factories.MessageFormFactory;

@ActiveProfiles("test")
@SpringBootTest
public class MessageFormUnitTest {
  private MessageForm messageForm;
  private BindingResult bindingResult;

  @BeforeEach
  public void setUp() {
    messageForm = MessageFormFactory.createMessage();
    bindingResult = Mockito.mock(BindingResult.class);
  }

  @Nested
  class メッセージが投稿できる場合 {
    @Test
    public void contentとimageが存在していれば保存できる () {
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, never()).rejectValue(anyString(),anyString(), anyString() );
    }

    @Test
    public void contentが空でも保存できる () {
      messageForm.setContent("");
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, never()).rejectValue(anyString(),anyString(), anyString());
    }

    @Test
    public void contentがnullでも保存できる () {
      messageForm.setContent(null);
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void imageが空でも保存できる () {
      MockMultipartFile emptyFile = new MockMultipartFile("image" , "empty.jpg", "image/jpeg", new byte[0]);
      messageForm.setImage(emptyFile);
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void imageがnullでも保存できる () {
      messageForm.setImage(null);
      messageForm.validateMessage(bindingResult);
      verify(bindingResult, never()).rejectValue(anyString(), anyString(), anyString());
    }
  }

  @Nested
  class メッセージが投稿できない場合 {
    @Test
    public void contentが空かつimageが空ファイルだと保存できない() {
       messageForm.setContent("");
       MockMultipartFile emptyFile = new MockMultipartFile("image" , "empty.jpg", "image/jpeg", new byte[0]);
       messageForm.setImage(emptyFile);
       messageForm.validateMessage(bindingResult);
       verify(bindingResult).rejectValue("content", "error.Message", "Please enter either content or image");
    }

    @Test
    public void contentが空かつimageがnullだと保存できない() {
      messageForm.setContent("");
      messageForm.setImage(null);
      messageForm.validateMessage(bindingResult);
      verify(bindingResult).rejectValue("content", "error.Message", "Please enter either content or image");
    }

    @Test
    public void contentがnullかつimageが空ファイルだと保存できない() {
      messageForm.setContent(null);
      MockMultipartFile emptyFile = new MockMultipartFile("image" , "empty.jpg", "image/jpeg", new byte[0]);
      messageForm.setImage(emptyFile);
      messageForm.validateMessage(bindingResult);
  verify(bindingResult).rejectValue("content", "error.Message", "Please enter either content or image");
    }
    @Test
    public void contentがnullかつimageがnullだと保存できない() {
      //content何もないよ 
      messageForm.setContent(null);
      //imageも何もないよ
      messageForm.setImage(null);
      //チェック過程↓
      messageForm.validateMessage(bindingResult);
      //エラーが書かれたかの確認！
      verify(bindingResult).rejectValue("content","error.Message","Please enter either content or image");
  
    }
  }
}