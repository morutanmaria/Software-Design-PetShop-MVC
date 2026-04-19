package main.controller;

import main.dto.ItemDTO;
import main.dto.PetDTO;
import main.model.entity.Item;
import main.model.entity.Pet;
import main.export.CsvExportStrategy;
import main.export.ExportStrategy;
import main.export.JsonExportStrategy;
import main.export.XmlExportStrategy;
import main.model.service.ExportService;
import main.model.service.ItemService;
import main.model.service.PetService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class ExportController {

    private final PetService petService;
    private final ItemService itemService;
    private final ExportService exportService;
    private final Map<String, ExportStrategy> strategies;

    public ExportController(PetService petService, ItemService itemService, ExportService exportService, List<ExportStrategy> strategyList) {
        this.petService = petService;
        this.itemService = itemService;
        this.exportService = exportService;
        this.strategies = Map.of(
                "json", strategyList.stream().filter(s -> s instanceof JsonExportStrategy).findFirst().get(),
                "csv", strategyList.stream().filter(s -> s instanceof CsvExportStrategy).findFirst().get(),
                "xml", strategyList.stream().filter(s -> s instanceof XmlExportStrategy).findFirst().get()
        );
    }

    @GetMapping("/export/pets")
    public ResponseEntity<byte[]> exportPets(
            @RequestParam String format,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDir
    ) throws Exception {

        List<PetDTO> pets = petService.searchAndSort(name, sortField, sortDir)
                .stream()
                .map(this::toDTO)
                .toList();

        return buildFileResponse(pets, format, "pets");
    }

    @GetMapping("/export/items")
    public ResponseEntity<byte[]> exportItems(
            @RequestParam String format
    ) throws Exception {

        List<ItemDTO> items = itemService.findAll()
                .stream()
                .map(this::toDTO)
                .toList();

        return buildFileResponse(items, format, "items");
    }

    private ResponseEntity<byte[]> buildFileResponse(Object data, String format, String baseName) throws Exception {

        ExportStrategy strategy = strategies.get(format.toLowerCase());

        if (strategy == null) {
            throw new IllegalArgumentException("Invalid format: " + format);
        }

        byte[] fileData = strategy.export(data);

        String contentType;
        String filename;

        switch (format.toLowerCase()) {
            case "json" -> {
                contentType = "application/json";
                filename = baseName + ".json";
            }
            case "csv" -> {
                contentType = "text/csv";
                filename = baseName + ".csv";
            }
            case "xml" -> {
                contentType = "application/xml";
                filename = baseName + ".xml";
            }
            default -> throw new IllegalArgumentException("Invalid format");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(fileData);
    }

    private String toCsv(Object data) {

        StringBuilder sb = new StringBuilder();

        if (data instanceof List<?> list && !list.isEmpty()) {
            Object first = list.get(0);
            if (first instanceof PetDTO) {
                sb.append("id,name,species,breed,gender,age,price,available\n");

                for (PetDTO p : (List<PetDTO>) list) {
                    sb.append(p.getId()).append(",")
                            .append(p.getName()).append(",")
                            .append(p.getSpecies()).append(",")
                            .append(p.getBreed()).append(",")
                            .append(p.getGender()).append(",")
                            .append(p.getAge()).append(",")
                            .append(p.getPrice()).append(",")
                            .append(p.isAvailable())
                            .append("\n");
                }
            }

            else if (first instanceof ItemDTO) {
                sb.append("id,name,description,price,type,petName\n");

                for (ItemDTO i : (List<ItemDTO>) list) {
                    sb.append(i.getId()).append(",")
                            .append(i.getName()).append(",")
                            .append(i.getDescription()).append(",")
                            .append(i.getPrice()).append(",")
                            .append(i.getType()).append(",")
                            .append(i.getPetName())
                            .append("\n");
                }
            }
        }

        return sb.toString();
    }

    private PetDTO toDTO(Pet pet) {
        return new PetDTO(
                pet.getId(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                pet.getGender(),
                pet.getAge(),
                pet.getPrice(),
                pet.isAvailable()
        );
    }
    private ItemDTO toDTO(Item i) {
        return new ItemDTO(
        i.getId(),
        i.getName(),
        i.getDescription(),
        i.getPrice(),
        String.valueOf(i.getType()),
        i.getPet() != null ? i.getPet().getName() : null
        );
    }
}