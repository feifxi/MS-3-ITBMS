package sit.int204.itbmsbackend.dtos.brand;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BrandDto {
    private Integer id;
    private String name;
    private String websiteUrl;
    private String countryOfOrigin;
}
