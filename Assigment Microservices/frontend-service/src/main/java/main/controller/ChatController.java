package main.controller;

import main.dto.LoggedUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @GetMapping
    public String chatPage(Model model, HttpSession session) {
        LoggedUser user = (LoggedUser) session.getAttribute("loggedUser");
        if (user != null) {
            model.addAttribute("chatUsername", user.getUsername());
        }
        return "chat";
    }
}
