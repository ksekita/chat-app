package in.tech_camp.chat_app.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.chat_app.entity.MessageEntity;

@Mapper
public interface MessageRepository {
  
  @Insert("INSERT INTO messages (content, user_id, room_id) VALUES (#{content}, #{user.id},#{room.id})")
   @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(MessageEntity messageEntity);

  @Select("SELECT * FROM messages WHERE room_id = #{roomId}")
  @Results(value = {
    @Result(property = "createdAt", column ="created_at"),
    @Result(property = "user", column ="user_id",
      one= @One(select = "in.tech_camp.chat_app.repository.UserRepository.findById"))

  })
  List<MessageEntity>findByRoomId(Integer roomId);

}
