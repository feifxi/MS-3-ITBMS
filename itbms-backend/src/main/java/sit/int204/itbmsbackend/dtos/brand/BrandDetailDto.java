package sit.int204.itbmsbackend.dtos.brand;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 30)
    private String name;

    @Size(max = 40, message = "websiteUrl must less than 40 characters")
    private String websiteUrl;

    private Boolean isActive;

    @Size(max = 80, message = "countryOfOrigin must less than 80 characters")
    private String countryOfOrigin;
    @JsonIgnore
    private Set<SaleItem> saleItems = new LinkedHashSet<>();

    public Integer getNoOfSaleItems () {
        return saleItems.size();
    };
    private Instant createdOn;
    private Instant updatedOn;
}