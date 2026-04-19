package main.dto;

public class ItemDTO {
    public Integer id;
    public String name;
    public String description;
    public Double price;
    public String type;
    public String petName;

    public ItemDTO(Integer id, String name, String description, Double price, String type, String petName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.petName = petName;
    }

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Double getPrice() {
        return price;
    }
    public String getType() {
        return type;
    }
    public String getPetName() {
        return petName;
    }
}