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
public class ListSaleItemRes {
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

