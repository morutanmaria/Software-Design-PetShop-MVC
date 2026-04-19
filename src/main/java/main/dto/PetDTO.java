package main.dto;

import java.math.BigDecimal;

public class PetDTO {
    private Integer id;
    private String name;
    private String species;
    private String breed;
    private String gender;
    private Integer age;
    private BigDecimal price;
    private boolean available;

    public PetDTO(Integer id, String name, String species, String breed,
                  String gender, Integer age, BigDecimal price, boolean available) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.gender = gender;
        this.age = age;
        this.price = price;
        this.available = available;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    public String getBreed() { return breed; }
    public String getGender() { return gender; }
    public Integer getAge() { return age; }
    public BigDecimal getPrice() { return price; }
    public boolean isAvailable() { return available; }
}