package sit.int204.itbmsbackend.dtos.saleItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sit.int204.itbmsbackend.entities.Brand;
import sit.int204.itbmsbackend.utils.Utils;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class UpdateSaleItemReq {
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
        this.model = Utils.validateString(model);
    }

    public void setDescription(String description) {
        this.description = Utils.validateString(description);
    }

    public void setColor(String color) {
        this.color = Utils.validateString(color);
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity == null || quantity < 0 ? 1 : quantity;
    }
}
