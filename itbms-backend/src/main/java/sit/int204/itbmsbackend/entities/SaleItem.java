package sit.int204.itbmsbackend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sale_items")
public class SaleItem {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "model", nullable = false, length = 60)
    private String model;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "ram_Gb")
    private Integer ramGb;

    @Column(name = "screen_size_inch", precision = 3, scale = 1)
    private BigDecimal screenSizeInch;

    @Column(name = "storage_Gb")
    private Integer storageGb;

    @Lob
    @Column(name = "color")
    private String color;

    @ColumnDefault("1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_on")
    private Instant createdOn;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_on")
    private Instant updatedOn;

}