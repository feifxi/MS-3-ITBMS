package sit.int204.itbmsbackend.dto.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import sit.int204.itbmsbackend.util.Utils;

@Data
public class BrandCreateUpdateDto {
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 30)
    private String name;

    @Size(max = 40, message = "websiteUrl must less than 40 characters")
    private String websiteUrl;

    private Boolean isActive;

    @Size(max = 80, message = "countryOfOrigin must less than 80 characters")
    private String countryOfOrigin;

    public void setName(String name) {
        this.name = name.trim();
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive != null ? isActive : false;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = Utils.trimOrSetNull(websiteUrl);
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = Utils.trimOrSetNull(countryOfOrigin);
    }
}
