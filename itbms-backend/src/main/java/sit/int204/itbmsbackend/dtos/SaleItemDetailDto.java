package sit.int204.itbmsbackend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import sit.int204.itbmsbackend.entities.Brand;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Getter
@Setter
public class SaleItemDetailDto {
    private Integer id;
    private String model;
    private String description;
    private BigDecimal price;
    private Integer ramGb;
    private BigDecimal screenSizeInch;
    private Integer storageGb;
    private String color;
    private Integer quantity;
    @JsonIgnore
    private Brand brand;
    private String getBrandName() {
        return brand.getName();
    }
}
