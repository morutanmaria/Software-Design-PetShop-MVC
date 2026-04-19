package main.model.service;

import main.model.entity.User;
import jakarta.servlet.http.HttpSession;
import main.event.EventListener;
import main.event.ResourceEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService implements EventListener {

    private final HttpSession session;
    private final JavaMailSender mailSender;

    public NotificationService(HttpSession session, JavaMailSender mailSender) {
        this.session = session;
        this.mailSender = mailSender;
    }

    @Override
    public void update(ResourceEvent event) {

        User user = (User) session.getAttribute("loggedUser");

        if (user != null && user.getEmail() != null) {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("PetShop Notification");

            message.setText(
                    "Entity: " + event.getEntity() + "\n" + "Name: " + event.getName() + "\n" + "Action: " + event.getType() + "\n" + "System event executed successfully."
            );

            mailSender.send(message);
        }
    }
}