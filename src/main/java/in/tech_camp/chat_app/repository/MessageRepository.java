package in.tech_camp.chat_app.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import in.tech_camp.chat_app.entity.MessageEntity;

@Mapper
public interface MessageRepository {
  
  @Insert("INSERT INTO messages (content, user_id, room_id) VALUES (#{content}, #{user.id},#{room.id})")
   @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(MessageEntity messageEntity);
}
