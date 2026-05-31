package main.client;

import main.dto.ItemDTO;
import main.dto.PetDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class ItemClient {

    private final RestTemplate restTemplate;
    private final String itemServiceUrl;
    private final String petServiceUrl;

    public ItemClient(RestTemplate restTemplate,
                      @Value("${item.service.url}") String itemServiceUrl,
                      @Value("${pet.service.url}") String petServiceUrl) {
        this.restTemplate = restTemplate;
        this.itemServiceUrl = itemServiceUrl;
        this.petServiceUrl = petServiceUrl;
    }


    public List<ItemDTO> getAllItems() {
        try {
            ItemDTO[] items = restTemplate.getForObject(
                    itemServiceUrl + "/items", ItemDTO[].class);
            return items != null ? Arrays.asList(items) : Collections.emptyList();
        } catch (ResourceAccessException e) {
            System.err.println("item-service unavailable: " + e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Could not fetch items: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public ItemDTO getItemById(Integer id) {
        try {
            return restTemplate.getForObject(
                    itemServiceUrl + "/items/" + id, ItemDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            System.err.println("Could not fetch item " + id + ": " + e.getMessage());
            return null;
        }
    }

    public void createItem(String name, String description, double price,
                           String type, String imagePath, Integer petId) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("description", description);
        body.put("price", price);
        body.put("type", type);
        body.put("imagePath", imagePath);
        body.put("petId", petId);

        restTemplate.postForObject(itemServiceUrl + "/items", body, ItemDTO.class);
    }

    public void updateItem(Integer id, String name, String description,
                           double price, String type, String imagePath, Integer petId) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("description", description);
        body.put("price", price);
        body.put("type", type);
        body.put("imagePath", imagePath);
        body.put("petId", petId);

        restTemplate.put(itemServiceUrl + "/items/" + id, body);
    }

    public void deleteItem(Integer id) {
        restTemplate.delete(itemServiceUrl + "/items/" + id);
    }

    public List<PetDTO> getAvailablePets() {
        try {
            PetDTO[] pets = restTemplate.getForObject(
                    petServiceUrl + "/pets", PetDTO[].class);
            return pets != null ? Arrays.asList(pets) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Could not fetch pets for dropdown: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
