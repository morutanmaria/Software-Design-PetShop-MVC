package main.controller;

import main.client.ItemClient;
import main.dto.ItemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemApiController {

    private final ItemClient itemClient;

    public ItemApiController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping
    public List<ItemDTO> getAllItems() {
        return itemClient.getAllItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getById(@PathVariable Integer id) {
        ItemDTO item = itemClient.getItemById(id);
        return item != null ? ResponseEntity.ok(item) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        itemClient.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-pet/{petId}")
    public List<ItemDTO> byPet(@PathVariable Integer petId) {
        return itemClient.getAllItems().stream()
                .filter(i -> petId.equals(i.getPetId()))
                .toList();
    }
}
