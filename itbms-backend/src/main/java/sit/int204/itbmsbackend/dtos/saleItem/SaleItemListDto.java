package sit.int204.itbmsbackend.dtos.saleItem;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sit.int204.itbmsbackend.entities.Brand;

@Data
@Getter
@Setter
public class SaleItemListDto {
    private Integer id;
    private String model;
    private BigDecimal price;
    private Integer ramGb;
    private Integer storageGb;
    private String color;
    @JsonIgnore
    private Brand brand;

    public String getBrandName() {
        return brand.getName();
    }
}

