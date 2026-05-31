package main.controller;

import main.client.PetClient;
import main.dto.LoggedUser;
import main.dto.PetDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/pets")
public class PetController {

    private final PetClient petClient;

    public PetController(PetClient petClient) {
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
                ("ADMIN".equals(u.getRole())
                        || "SELLER".equals(u.getRole()));
    }

    @GetMapping
    public String petsPage(Model model,
                           HttpSession session) {

        model.addAttribute("isAdmin", isAdmin(session));
        model.addAttribute("isAdminOrSeller", isAdminOrSeller(session));

        return "pets";
    }

    @GetMapping("/add")
    public String addPetPage(HttpSession session) {

        if (!isAdminOrSeller(session))
            return "redirect:/login";

        return "pet-form";
    }

    @GetMapping("/edit/{id}")
    public String editPetPage(@PathVariable Integer id,
                              Model model,
                              HttpSession session) {

        if (!isAdmin(session))
            return "redirect:/login";

        PetDTO pet = petClient.getPetById(id);

        if (pet == null)
            return "redirect:/pets";

        model.addAttribute("pet", pet);

        return "pet-edit";
    }
    @PostMapping("/update")
    public String updatePet(@RequestParam Integer id,
                            @RequestParam String name,
                            @RequestParam String species,
                            @RequestParam String breed,
                            @RequestParam Integer age,
                            @RequestParam Double price,
                            HttpSession session,
                            Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            petClient.updatePet(id, name, species, breed, age, price);
            return "redirect:/pets";
        } catch (Exception e) {
            PetDTO fallbackPet = new PetDTO();
            fallbackPet.setId(id);
            fallbackPet.setName(name);
            fallbackPet.setSpecies(species);
            fallbackPet.setBreed(breed);
            fallbackPet.setAge(age);
            fallbackPet.setPrice(BigDecimal.valueOf(price));

            model.addAttribute("pet", fallbackPet);
            model.addAttribute("errorMessage", "Failed to update pet: " + e.getMessage());
            return "pet-edit";
        }
    }

    @PostMapping("/add")
    public String createPet(@RequestParam String name,
                            @RequestParam String species,
                            @RequestParam String breed,
                            @RequestParam Integer age,
                            @RequestParam Double price,
                            HttpSession session,
                            Model model) {
        if (!isAdminOrSeller(session)) {
            return "redirect:/login";
        }

        try {
            petClient.createPet(name, species, breed, age, price);
            return "redirect:/pets";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to create pet: " + e.getMessage());
            return "pet-form";
        }
    }
}