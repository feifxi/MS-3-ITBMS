package sit.int204.itbmsbackend.dto.saleItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import sit.int204.itbmsbackend.dto.order.SellerResponse;
import sit.int204.itbmsbackend.dto.saleItemImage.SaleItemImageDTO;
import sit.int204.itbmsbackend.entity.Brand;
import sit.int204.itbmsbackend.entity.SaleItemImage;
import sit.int204.itbmsbackend.entity.User;

@Data
public class SaleItemListResponseDTO {
    private Integer id;
    private String model;
    private BigDecimal price;
    private Integer ramGb;
    private Integer storageGb;
    private String color;
    private Integer quantity;
    @JsonIgnore
    private Brand brand;
    private List<SaleItemImage> saleItemImages = new ArrayList<>();
    private User seller;

    public Integer getSellerId() {
        return this.seller.getId();
    }

    public SellerResponse getSeller() {
        SellerResponse sellerResponse = new SellerResponse();
        sellerResponse.setId(this.seller.getId());
        sellerResponse.setNickname(this.seller.getNickname());
        sellerResponse.setFullName(this.seller.getFullName());
        sellerResponse.setEmail(this.seller.getEmail());
        sellerResponse.setPhone(this.seller.getPhone());
        sellerResponse.setShopName(this.seller.getShopName());
        return sellerResponse;
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

    public String getBrandName() {
        return brand.getName();
    }
}

