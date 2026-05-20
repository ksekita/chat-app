package in.tech_camp.chat_app.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

import in.tech_camp.chat_app.entity.RoomEntity;

@Mapper
public interface RoomRepository {
  @Insert("INSERT INTO rooms(name) VALUES(#{name})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(RoomEntity roomEntity);
  
}
