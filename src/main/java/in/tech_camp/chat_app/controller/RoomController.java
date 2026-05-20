package in.tech_camp.chat_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import in.tech_camp.chat_app.form.RoomForm;

@Controller
public class RoomController {
  
  @GetMapping("/rooms/new")
  public String showNewRoomForm(Model model){
    model.addAttribute("roomForm", new RoomForm());
    return "rooms/new";
  }
}
