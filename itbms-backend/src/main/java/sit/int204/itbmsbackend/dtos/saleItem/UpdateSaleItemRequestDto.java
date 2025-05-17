package sit.int204.itbmsbackend.dtos.saleItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sit.int204.itbmsbackend.entities.Brand;
import sit.int204.itbmsbackend.utils.Utils;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class UpdateSaleItemRequestDto {
    @JsonIgnore
    private Integer id;

    @NotNull(message = "Model is required")
    @NotEmpty
    private String model;

    @NotNull(message = "Description is required")
    @NotEmpty
    private String description;

    @Min(0)
    @NotNull(message = "Price is required")
    private BigDecimal price;

    @Min(value = 0, message = "ramGb must be greater than 0")
    private Integer ramGb;

    @Min(value = 0, message = "screenSizeInch must be greater than 0")
    private BigDecimal screenSizeInch;

    @Min(value = 0, message = "storageGb must be greater than 0")
    private Integer storageGb;

    private String color;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "quantity must be greater than 0")
    private Integer quantity;

    @NotNull(message = "Brand is required")
    private Brand brand;

    public void setModel(String model) {
        this.model = Utils.trimOrSetNull(model);
    }

    public void setDescription(String description) {
        this.description = Utils.trimOrSetNull(description);
    }

    public void setColor(String color) {
        this.color = Utils.trimOrSetNull(color);
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity == null || quantity < 0 ? 1 : quantity;
    }
}
