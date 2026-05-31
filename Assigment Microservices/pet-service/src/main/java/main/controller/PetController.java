package main.controller;

import main.dto.PetDTO;
import main.dto.PetRequest;
import main.model.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
@CrossOrigin(origins = "${frontend.service.url:http://localhost:8080}")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public ResponseEntity<List<PetDTO>> getAllPets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "asc") String sortDir) {

        List<PetDTO> pets = petService.searchAndSort(search, sortField, sortDir);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPetById(@PathVariable Integer id) {
        return petService.getPetById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<PetDTO> createPet(@RequestBody PetRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Pet name must not be empty");
        }
        PetDTO created = petService.savePet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetDTO> updatePet(@PathVariable Integer id,
                                            @RequestBody PetRequest request) {
        PetDTO updated = petService.updatePet(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Integer id) {
        petService.deletePetById(id);
        return ResponseEntity.noContent().build();
    }
}
