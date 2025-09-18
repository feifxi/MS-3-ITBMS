package sit.int204.itbmsbackend.config;

// FileStorageProperties.java
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileStorageProperties {
    private String uploadDir;
    private String maxSize;
    private String allowedTypes;
}

