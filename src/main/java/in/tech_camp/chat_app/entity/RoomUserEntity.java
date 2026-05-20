package in.tech_camp.chat_app.entity;

import lombok.Data;

@Data
public class RoomUserEntity {
  private Long id;
  private UserEntity user;
  private RoomEntity room;
}

