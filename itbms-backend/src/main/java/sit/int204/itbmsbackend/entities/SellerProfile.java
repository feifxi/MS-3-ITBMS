package sit.int204.itbmsbackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "seller_profiles")
public class SellerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 255)
    @NotNull
    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Size(max = 20)
    @NotNull
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Size(max = 50)
    @NotNull
    @Column(name = "bank_account_number", nullable = false, length = 50)
    private String bankAccountNumber;

    @Size(max = 50)
    @NotNull
    @Column(name = "bank_name", nullable = false, length = 50)
    private String bankName;

    @Size(max = 20)
    @Column(name = "national_id", length = 20)
    private String nationalId;

    @Size(max = 255)
    @Column(name = "national_id_image_front")
    private String nationalIdImageFront;

    @Size(max = 255)
    @Column(name = "national_id_image_back")
    private String nationalIdImageBack;

}