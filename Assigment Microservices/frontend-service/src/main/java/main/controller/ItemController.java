package main.controller;

import main.client.ItemClient;
import main.client.PetClient;
import main.dto.ItemDTO;
import main.dto.LoggedUser;
import main.dto.PetDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;
    private final PetClient petClient;

    public ItemController(ItemClient itemClient, PetClient petClient) {
        this.itemClient = itemClient;
        this.petClient = petClient;
    }

    private LoggedUser getLoggedUser(HttpSession session) {
        return (LoggedUser) session.getAttribute("loggedUser");
    }

    private boolean isAdmin(HttpSession session) {
        LoggedUser u = getLoggedUser(session);
        return u != null && "ADMIN".equals(u.getRole());
    }

    private boolean isAdminOrSeller(HttpSession session) {
        LoggedUser u = getLoggedUser(session);
        return u != null &&
                ("ADMIN".equals(u.getRole()) || "SELLER".equals(u.getRole()));
    }

    @GetMapping
    public String itemsPage(Model model, HttpSession session) {
        model.addAttribute("loggedUser", getLoggedUser(session));
        model.addAttribute("isAdmin", isAdmin(session));
        model.addAttribute("isAdminOrSeller", isAdminOrSeller(session));

        return "items";
    }

    @GetMapping("/add")
    public String addItemPage(Model model, HttpSession session) {
        if (!isAdminOrSeller(session)) {
            return "redirect:/login";
        }

        List<PetDTO> availablePets = itemClient.getAvailablePets();
        model.addAttribute("pets", availablePets);
        model.addAttribute("currentLang", session.getAttribute("lang") != null ? session.getAttribute("lang") : "en");

        return "add-item";
    }

    @PostMapping("/add")
    public String createItem(@RequestParam String name,
                             @RequestParam(required = false) String description,
                             @RequestParam double price,
                             @RequestParam String type,
                             @RequestParam(required = false) String imagePath,
                             @RequestParam(required = false) Integer petId,
                             HttpSession session,
                             Model model) {
        if (!isAdminOrSeller(session)) {
            return "redirect:/login";
        }

        try {
            itemClient.createItem(name, description, price, type, imagePath, petId);
            return "redirect:/items";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to create item: " + e.getMessage());
            model.addAttribute("pets", itemClient.getAvailablePets());
            return "add-item";
        }
    }

    @GetMapping("/edit/{id}")
    public String editItemPage(@PathVariable Integer id, Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        ItemDTO item = itemClient.getItemById(id);
        if (item == null) {
            return "redirect:/items";
        }

        model.addAttribute("item", item);
        model.addAttribute("pets", itemClient.getAvailablePets());
        model.addAttribute("currentLang", session.getAttribute("lang") != null ? session.getAttribute("lang") : "en");

        return "edit-item";
    }

    @PostMapping("/update")
    public String updateItem(@RequestParam Integer id,
                             @RequestParam String name,
                             @RequestParam(required = false) String description,
                             @RequestParam double price,
                             @RequestParam String type,
                             @RequestParam(required = false) String imagePath,
                             @RequestParam(required = false) Integer petId,
                             HttpSession session,
                             Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            itemClient.updateItem(id, name, description, price, type, imagePath, petId);
            return "redirect:/items";
        } catch (Exception e) {
            ItemDTO fallbackDto = new ItemDTO();
            fallbackDto.setId(id);
            fallbackDto.setName(name);
            fallbackDto.setDescription(description);
            fallbackDto.setPrice(price);
            fallbackDto.setType(type);
            fallbackDto.setPetId(petId);

            model.addAttribute("item", fallbackDto);
            model.addAttribute("pets", itemClient.getAvailablePets());
            model.addAttribute("errorMessage", "Failed to update item: " + e.getMessage());
            return "edit-item";
        }
    }
}