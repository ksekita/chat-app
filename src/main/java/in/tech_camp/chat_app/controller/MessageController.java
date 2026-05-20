package in.tech_camp.chat_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import in.tech_camp.chat_app.custom_user.CustomUserDetail;
import in.tech_camp.chat_app.entity.RoomEntity;
import in.tech_camp.chat_app.entity.RoomUserEntity;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.repository.UserRepository;
import in.tech_camp.chat_app.repository.RoomUserRepository;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class MessageController {
  private final UserRepository userRepository;
   private final RoomUserRepository roomUserRepository;


  @GetMapping("/message")
  public String showMessages(@AuthenticationPrincipal CustomUserDetail currentUser, Model model){
    UserEntity user = userRepository.findById(currentUser.getId());
    model.addAttribute("user", user);
     List<RoomUserEntity> roomUserEntities = roomUserRepository.findByUserId(currentUser.getId());
    List<RoomEntity> roomList = roomUserEntities.stream()
        .map(RoomUserEntity::getRoom)
        .collect(Collectors.toList());
    model.addAttribute("rooms", roomList);
    return "messages/index";
  }
}
