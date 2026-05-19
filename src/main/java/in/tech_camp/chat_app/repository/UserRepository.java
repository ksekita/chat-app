package in.tech_camp.chat_app.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.chat_app.entity.UserEntity;

@Mapper
public interface UserRepository {
  
  @Insert("INSERT INTO users (name, email, password) VaLUES (#{name},#{email},#{password})")
  @Options(useGeneratedKeys = true, keyProperty ="id")
  void insert(UserEntity user);

  @Select("SELECT * FROM users WHERE email = #{email}")
  UserEntity findByEmail(String email);
}
