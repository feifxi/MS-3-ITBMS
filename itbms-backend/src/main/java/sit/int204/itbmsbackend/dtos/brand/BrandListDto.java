package sit.int204.itbmsbackend.dtos.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
