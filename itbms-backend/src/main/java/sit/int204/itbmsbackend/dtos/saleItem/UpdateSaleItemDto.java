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
public class UpdateSaleItemDto {
    private Integer id;
    private String model;
    private String description;
    private BigDecimal price;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer storageGb;
    private String color;
    private Integer quantity;
    private Integer brandId;
    @JsonIgnore
    private Brand brand;
}
