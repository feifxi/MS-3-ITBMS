package sit.int204.itbmsbackend.dtos.brand;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sit.int204.itbmsbackend.entities.SaleItem;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Getter
@Setter
public class BrandDetailDto {
    private Integer id;
    private String name;
    private String websiteUrl;
    private Boolean isActive;
    private String countryOfOrigin;
    @JsonIgnore
    private Set<SaleItem> saleItems = new LinkedHashSet<>();

    public Integer getNoOfSaleItems () {
        return saleItems.size();
    };
    private Instant createdOn;
    private Instant updatedOn;
}