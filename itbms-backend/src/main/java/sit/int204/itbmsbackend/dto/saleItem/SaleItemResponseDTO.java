package sit.int204.itbmsbackend.dto.saleItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import sit.int204.itbmsbackend.dto.saleItemImage.SaleItemImageDTO;
import sit.int204.itbmsbackend.entity.Brand;
import sit.int204.itbmsbackend.entity.SaleItemImage;
import sit.int204.itbmsbackend.entity.User;

@Data
public class SaleItemResponseDTO {
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
    private Instant createdOn;
    private Instant updatedOn;
    private List<SaleItemImage> saleItemImages = new ArrayList<>();
    @JsonIgnore
    private User seller;

    public String getBrandName() {
        return brand.getName();
    }

    public Integer getSellerId() {
        return this.seller.getId();
    }

    public List<SaleItemImageDTO> getSaleItemImages() {
        return saleItemImages.stream()
                .sorted(Comparator.comparing(SaleItemImage::getImageViewOrder))
                .map((saleItemImage -> {
                    SaleItemImageDTO dto = new SaleItemImageDTO();
                    dto.setImageName(saleItemImage.getImageName());
                    dto.setOriginalImageName(saleItemImage.getOriginalImageName());
                    dto.setImageViewOrder(saleItemImage.getImageViewOrder());
                    return dto;
                }))
                .collect(Collectors.toList());
    }
}
