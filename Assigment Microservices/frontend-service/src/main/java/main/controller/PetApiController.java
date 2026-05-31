package main.controller;

import main.client.PetClient;
import main.dto.PetDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetApiController {

    private final PetClient petClient;

    public PetApiController(PetClient petClient) {
        this.petClient = petClient;
    }

    @GetMapping
    public List<PetDTO> getAllPets(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir) {

        return petClient.getAllPets(
                search,
                sortField,
                sortDir
        );
    }

    @GetMapping("/{id}")
    public PetDTO getPet(@PathVariable Integer id) {
        return petClient.getPetById(id);
    }
}