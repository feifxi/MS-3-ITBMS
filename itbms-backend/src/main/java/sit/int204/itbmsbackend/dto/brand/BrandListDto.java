package sit.int204.itbmsbackend.dto.brand;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BrandListDto {
    private Integer id;

    private String name;

    private String websiteUrl;

    private Boolean isActive;

    private String countryOfOrigin;
}
