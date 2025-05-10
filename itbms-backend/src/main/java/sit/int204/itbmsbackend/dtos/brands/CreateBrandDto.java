package sit.int204.itbmsbackend.dtos.brands;

import lombok.Data;

@Data
public class CreateBrandDto {
    private String name;
    private String websiteUrl;
    private Boolean isActive;
    private String countryOfOrigin;

}
