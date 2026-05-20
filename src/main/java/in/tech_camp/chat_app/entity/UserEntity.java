package in.tech_camp.chat_app.entity;
import java.util.List;

import lombok.Data;


@Data
public class UserEntity {
  private Integer id;
  private String name;
  private String email;
  private String password;
  private List<RoomUserEntity> roomUsers;
}
