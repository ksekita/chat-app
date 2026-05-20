package in.tech_camp.chat_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.chat_app.custom_user.CustomUserDetail;
import in.tech_camp.chat_app.entity.RoomEntity;
import in.tech_camp.chat_app.entity.RoomUserEntity;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.form.RoomForm;
import in.tech_camp.chat_app.repository.RoomRepository;
import in.tech_camp.chat_app.repository.RoomUserRepository;
import in.tech_camp.chat_app.repository.UserRepository;
import in.tech_camp.chat_app.validation.ValidationOrder;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class RoomController {
  private final UserRepository userRepository;

    private final RoomRepository roomRepository;

  private final RoomUserRepository roomUserRepository;

  @GetMapping("/")
   public String index(@AuthenticationPrincipal CustomUserDetail currentUser, Model model) {
    UserEntity user = userRepository.findById(currentUser.getId());
    model.addAttribute("user", user);
    List<RoomUserEntity> roomUserEntities = roomUserRepository.findByUserId(currentUser.getId());
    List<RoomEntity> roomList = roomUserEntities.stream()
        .map(RoomUserEntity::getRoom)
        .collect(Collectors.toList());
    model.addAttribute("rooms", roomList);
    return "rooms/index";
  }
  @GetMapping("/rooms/new")
  public String showRoomNew(@AuthenticationPrincipal CustomUserDetail currentUser,Model model){
    List<UserEntity> users = userRepository.findAllExcept(currentUser.getId());
    model.addAttribute("users",users);
    model.addAttribute("roomForm", new RoomForm());
    return "rooms/new";
  }

 @PostMapping("/rooms")
  public String createRoom(
      @ModelAttribute("roomForm") @Validated(ValidationOrder.class) RoomForm roomForm, 
      BindingResult bindingResult, 
      @AuthenticationPrincipal CustomUserDetail currentUser, 
      Model model) {
    
    // ==========================================
    // 1. バリデーション（入力チェックで門前払い）
    // ==========================================
    if (bindingResult.hasErrors()) {
      List<String> errorMessages = bindingResult.getAllErrors().stream()
                              .map(DefaultMessageSourceResolvable::getDefaultMessage)
                              .collect(Collectors.toList());
      List<UserEntity> users = userRepository.findAllExcept(currentUser.getId());
      model.addAttribute("users", users);
      model.addAttribute("roomForm", roomForm);
      model.addAttribute("errorMessages", errorMessages);
      return "rooms/new";
    }

    // ==========================================
    // 2. ルーム自体の保存（roomsテーブル）
    // ==========================================
    RoomEntity roomEntity = new RoomEntity();
    roomEntity.setName(roomForm.getName());
    try {
      roomRepository.insert(roomEntity);
    } catch (Exception e) {
      System.out.println("エラー：" + e);
      List<UserEntity> users = userRepository.findAllExcept(currentUser.getId());
      model.addAttribute("users", users);
      model.addAttribute("roomForm", new RoomForm());
      return "rooms/new";
    }

    // ==========================================
    // 3. 【省略されていた部分】中間テーブルへの保存（room_usersテーブル）
    // ==========================================
    List<Integer> memberIds = roomForm.getMemberIds(); // 画面でチェックされたユーザーIDのリスト
    
    for (Integer userId : memberIds) { // メンバーの人数分だけ繰り返す！
      UserEntity userEntity = userRepository.findById(userId); // ユーザー情報を探してくる
      RoomUserEntity roomUserEntity = new RoomUserEntity(); // 中間テーブル用の箱を作る
      
      roomUserEntity.setRoom(roomEntity); // ②で作ったルームをセット
      roomUserEntity.setUser(userEntity); // 探してきたユーザーをセット
      
      try {
        roomUserRepository.insert(roomUserEntity); // 中間テーブルに1件ずつ保存！
      } catch (Exception e) {
        System.out.println("エラー：" + e);
        List<UserEntity> users = userRepository.findAllExcept(currentUser.getId());
        model.addAttribute("users", users);
        model.addAttribute("roomForm", new RoomForm());
        return "rooms/new";
      }
    }

    // ==========================================
    // 4. 全部成功したらトップページへ移動！
    // ==========================================
    return "redirect:/";
  }
}
