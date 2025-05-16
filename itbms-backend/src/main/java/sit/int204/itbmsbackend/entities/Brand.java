package sit.int204.itbmsbackend.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "website_url", length = 40)
    private String websiteUrl;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "country_of_origin", length = 80)
    private String countryOfOrigin;

    @Column(name = "created_on", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdOn;

    @Column(name = "updated_on", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedOn;


    @OneToMany(mappedBy = "brand")
    private Set<SaleItem> saleItems = new LinkedHashSet<>();

    @PrePersist
    public void prePersist() {
        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }
        updatedOn = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedOn = LocalDateTime.now();
    }


}