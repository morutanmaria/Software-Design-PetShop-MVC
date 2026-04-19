package main.config;

import main.event.EventManager;
import main.model.service.NotificationService;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class EventConfig {
    private final EventManager eventManager;
    private final NotificationService notificationService;

    public EventConfig(EventManager eventManager, NotificationService notificationService) {
        this.eventManager = eventManager;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void init() {
        eventManager.subscribe(notificationService);
    }
}