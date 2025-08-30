package sit.int204.itbmsbackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 40)
    @NotNull
    @Column(name = "nickname", nullable = false, length = 40)
    private String nickname;

    @Size(max = 40)
    @NotNull
    @Column(name = "full_name", nullable = false, length = 40)
    private String fullName;

    @Size(max = 100)
    @NotNull
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Size(max = 255)
    @Column(name = "shop_name")
    private String shopName;

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @Size(max = 50)
    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Size(max = 50)
    @Column(name = "bank_name", length = 50)
    private String bankName;

    @Size(max = 20)
    @Column(name = "id_card_number", length = 20)
    private String idCardNumber;

    @Size(max = 255)
    @Column(name = "id_card_image_front")
    private String idCardImageFront;

    @Size(max = 255)
    @Column(name = "id_card_image_back")
    private String idCardImageBack;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false;

    @ColumnDefault("'BUYER'")
    @Lob
    @Column(name = "user_type")
    private String userType;

    @ColumnDefault("'INACTIVE'")
    @Lob
    @Column(name = "status")
    private String status;

    @Size(max = 255)
    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    @NotNull
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @NotNull
    @Column(name = "updated_on", nullable = false)
    private LocalDateTime updatedOn;

    @OneToMany
    @JoinColumn(name = "user_id")
    private Set<Address> addresses = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

    @PrePersist
    protected void onCreate() {
        status = "INACTIVE";
        isLocked = false;
        createdOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }

    public Set<String> getRolesStr() {
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }
}