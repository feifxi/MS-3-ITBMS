package sit.int204.itbmsbackend.dto.saleItemImage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import sit.int204.itbmsbackend.entity.SaleItem;

@Data
public class SaleItemImageDTO {
    @JsonIgnore
    private Integer id;
    @JsonIgnore
    private String imageName;
    @JsonIgnore
    private String originalImageName;
    @JsonIgnore
    private SaleItem saleItem;
    private Integer imageViewOrder;
    public String getFileName() {
        return imageName;
    }
    public String getOriginalFileName() {
        return originalImageName;
    }
}
