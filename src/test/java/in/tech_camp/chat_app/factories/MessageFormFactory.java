package in.tech_camp.chat_app.factories;

import org.springframework.mock.web.MockMultipartFile;

import com.github.javafaker.Faker;

import in.tech_camp.chat_app.form.MessageForm;

public class MessageFormFactory {
  private static final Faker faker = new Faker();
  
  public static MessageForm createMessage(){
    MessageForm messageForm = new MessageForm();

    messageForm.setContent(faker.lorem().sentence());
    messageForm.setImage(new MockMultipartFile("image", "image.jpg","image/jpeg",faker.avatar().image().getBytes()));

    return messageForm;
  }
}
