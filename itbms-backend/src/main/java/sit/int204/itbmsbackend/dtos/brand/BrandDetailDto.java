package sit.int204.itbmsbackend.dtos.brand;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class BrandDetailDto {
    private Integer id;
    private String name;
    private String websiteUrl;
    private Boolean isActive;
    private String countryOfOrigin;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}