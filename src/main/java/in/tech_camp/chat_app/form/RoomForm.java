package in.tech_camp.chat_app.form;

import java.util.List;

import lombok.Data;

@Data
public class RoomForm {
  private String name;
  private List<Integer> memberIds;
}
