package in.tech_camp.chat_app.factories;

import com.github.javafaker.Faker;

import in.tech_camp.chat_app.form.RoomForm;

public class RoomFormFactory {
  private static final Faker faker = new Faker();
  
  public static RoomForm createRoom() {
    RoomForm roomForm = new RoomForm();

    roomForm.setName(faker.lorem().sentence());

    return roomForm;
  }
}
