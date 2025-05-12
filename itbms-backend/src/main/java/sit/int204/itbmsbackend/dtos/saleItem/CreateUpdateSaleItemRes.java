package sit.int204.itbmsbackend.dtos.saleItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sit.int204.itbmsbackend.entities.Brand;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class CreateUpdateSaleItemRes {
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
    public String getBrandName() {
        return brand.getName();
    }
//    private Instant createdOn;
//    private Instant updatedOn;

    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}
