package main.export;


import main.dto.ItemDTO;
import main.dto.PetDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("csv")
public class CsvExportStrategy implements ExportStrategy {

    @Override
    public byte[] export(Object data) {

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

        return sb.toString().getBytes();
    }
}