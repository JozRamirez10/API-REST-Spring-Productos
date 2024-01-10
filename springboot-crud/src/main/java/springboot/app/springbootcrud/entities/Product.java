package springboot.app.springbootcrud.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import springboot.app.springbootcrud.validation.IsRequired;
import springboot.app.springbootcrud.validation.isExistsDb;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotEmpty(message = "{NotEmpty.product.name}") // Desde properties
    @Size(min = 3, max = 20)
    private String name;
    
    @Min(value = 500, message = "debe ser mayor o igual que 500")
    @NotNull(message = "{NotNull.product.price}")
    private Integer price;
    
    //@NotBlank (message = "{NotBlank.product.description}")
        // Valida que no sea " "
    @IsRequired // Usando clase personalizada
    private String description;

    @isExistsDb
    @IsRequired
    private String sku;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

}
