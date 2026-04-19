package main.event;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventManager {
    private final List<EventListener> listeners = new ArrayList<>();

    public void subscribe(EventListener listener) {
        listeners.add(listener);
    }

    public void notify(ResourceEvent event) {
        for (EventListener listener : listeners) {
            listener.update(event);
        }
    }
}