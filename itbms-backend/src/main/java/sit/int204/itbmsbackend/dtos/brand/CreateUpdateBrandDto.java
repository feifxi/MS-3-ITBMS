package sit.int204.itbmsbackend.dtos.brand;

import lombok.Data;
import sit.int204.itbmsbackend.utils.Utils;

@Data
public class CreateUpdateBrandDto {
    private String name;
    private String websiteUrl;
    private Boolean isActive;
    private String countryOfOrigin;

    public void setName(String name) {
        this.name = Utils.trimOrSetNull(name);
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = Utils.trimOrSetNull(websiteUrl);
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = Utils.trimOrSetNull(countryOfOrigin);
    }
}
