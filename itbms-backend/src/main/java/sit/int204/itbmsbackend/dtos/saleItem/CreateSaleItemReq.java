package sit.int204.itbmsbackend.dtos.saleItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sit.int204.itbmsbackend.entities.Brand;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class CreateSaleItemReq {
    @JsonIgnore
    private Integer id;
    private String model;
    private String description;
    private BigDecimal price;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer storageGb;
    private String color;
    private Integer quantity;
    private Brand brand;

    public void setModel(String model) {
        this.model = model.trim();
    }
    public void setDescription(String description) {
        this.description = description.trim();
    }
    public void setColor(String color) {
        this.color = color.trim();
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity <= 0 ? 1 : quantity;
    }
}
