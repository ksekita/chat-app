package in.tech_camp.chat_app.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import in.tech_camp.chat_app.entity.UserEntity;

@Mapper
public interface UserRepository {
  
  @Insert("INSERT INTO users (name, email, password) VaLUES (#{name},#{email},#{password})")
  @Options(useGeneratedKeys = true, keyProperty ="id")
  void insert(UserEntity user);

  @Select("SELECT * FROM users WHERE email = #{email}")
  UserEntity findByEmail(String email);

  @Select("SELECT * FROM users WHERE id = #{id}")
  UserEntity findById(Integer id);

  @Update("UPDATE users SET name = #{name}, email=#{email} WHERE id= #{id}")
  void update(UserEntity user);

  @Select("SELECT EXISTS(SELECT 1 FROM users WHERE email = #{email})")
  boolean existByEmail(String email);

  @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email} AND id != #{userId}")
  boolean existsByEmailExcludingCurrent(String email, Integer userId);
}
