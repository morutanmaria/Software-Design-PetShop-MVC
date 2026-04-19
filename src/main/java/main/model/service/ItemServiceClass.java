package main.model.service;

import main.event.EventManager;
import main.event.ResourceEvent;
import main.model.entity.Item;
import main.model.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceClass implements ItemService {

    private final ItemRepository itemRepository;
    private final EventManager eventManager;

    public ItemServiceClass(ItemRepository itemRepository, EventManager eventManager) {
        this.itemRepository = itemRepository;
        this.eventManager = eventManager;
    }

    @Override
    public Item save(Item item) {
        boolean isNew = (item.getId() == null);
        Item saved = itemRepository.save(item);
        String eventType = isNew ? "CREATED" : "UPDATED";

        eventManager.notify(new ResourceEvent(eventType, "Item", saved.getName(), saved));
        return saved;
    }

    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public Optional<Item> findById(Integer id) {
        return itemRepository.findById(id);
    }

    @Override
    public void deleteById(Integer id) {
        Item item = itemRepository.findById(id).orElse(null);
        assert item != null;
        String name = item.getName();
        itemRepository.deleteById(id);
        eventManager.notify(new ResourceEvent("DELETED", "Item", name, item));
    }
}