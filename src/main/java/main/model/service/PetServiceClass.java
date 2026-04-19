package main.model.service;

import main.event.EventManager;
import main.event.ResourceEvent;
import main.model.entity.Pet;
import main.model.repository.PetRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetServiceClass implements PetService{

    private final PetRepository petRepository;
    private final EventManager eventManager;

    public PetServiceClass(PetRepository petRepository, EventManager eventManager) {
        this.petRepository = petRepository;
        this.eventManager = eventManager;
    }
    @Override
    public Pet savePet(Pet pet) {
        Pet savedPet = petRepository.save(pet);
        eventManager.notify(new ResourceEvent("CREATED", "Pet", savedPet.getName(), savedPet));
        return savedPet;
    }
    @Override
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }
    @Override
    public List<Pet> searchPets(String name) {
        if (name == null || name.isEmpty()) {
            return petRepository.findAll();
        }
        return petRepository.findPetByNameContainingIgnoreCase(name);
    }
    @Override
    public Optional<Pet> getPetById(Integer id){
        return petRepository.findById(id);
    }
    @Override
    public void deletePetById(Integer id){
        Pet deletedPet = petRepository.findById(id).orElse(null);
        assert deletedPet != null;
        String name = deletedPet.getName();
        petRepository.deleteById(id);
        eventManager.notify(new ResourceEvent("DELETED", "Pet", name, deletedPet));
    }
    @Override
    public void updatePet(Pet pet){
        Pet savedPet = petRepository.save(pet);
        eventManager.notify(new ResourceEvent("UPDATED", "Pet", savedPet.getName(), savedPet));
    }

    public List<Pet> getAllPetsSorted(String sortField, String sortDir) {
        if (sortField == null || sortField.isBlank()) {
            return petRepository.findAll();
        }

        Sort.Direction direction =
                (sortDir != null && sortDir.equalsIgnoreCase("desc"))
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortField);

        return petRepository.findAll(sort);
    }

    public List<Pet> searchAndSort(String name, String sortField, String sortDir) {

        List<Pet> pets = searchPets(name);

        if (sortField == null || sortField.isBlank()) {
            return pets;
        }

        pets.sort((p1, p2) -> {
            int result = 0;

            switch (sortField) {
                case "name":
                    result = p1.getName().compareToIgnoreCase(p2.getName());
                    break;
                case "price":
                    result = p1.getPrice().compareTo(p2.getPrice());
                    break;
                case "age":
                    result = Integer.compare(p1.getAge(), p2.getAge());
                    break;
            }

            return "desc".equalsIgnoreCase(sortDir) ? -result : result;
        });

        return pets;
    }
}
