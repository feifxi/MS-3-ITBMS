package sit.int204.itbmsbackend.dtos.saleItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sit.int204.itbmsbackend.entities.Brand;
import sit.int204.itbmsbackend.entities.SaleItemImage;

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

    @JsonIgnore
    private List<SaleItemImage> saleItemImages = new ArrayList<>();

    public List<String> getImageNames() {
        return saleItemImages.stream()
                .sorted(Comparator.comparing(SaleItemImage::getOrderIndex))
                .map(SaleItemImage::getImageName)
                .collect(Collectors.toList());
    }

    public String getBrandName() {
        return brand.getName();
    }

    private String createdOn;
    private String updatedOn;
}
