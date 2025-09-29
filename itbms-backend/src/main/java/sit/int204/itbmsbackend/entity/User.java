package sit.int204.itbmsbackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import sit.int204.itbmsbackend.constant.UserStatus;
import sit.int204.itbmsbackend.constant.UserType;

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

    @NotNull
    @Column(name = "nickname", nullable = false, length = 40)
    private String nickname;

    @NotNull
    @Column(name = "full_name", nullable = false, length = 40)
    private String fullName;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "shop_name")
    private String shopName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Column(name = "bank_name", length = 50)
    private String bankName;

    @Column(name = "id_card_number", length = 20)
    private String idCardNumber;

    @Column(name = "id_card_image_front")
    private String idCardImageFront;

    @Column(name = "id_card_image_back")
    private String idCardImageBack;

    @ColumnDefault("0")
    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false;

    @ColumnDefault("'BUYER'")
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

    @ColumnDefault("'INACTIVE'")
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_on", nullable = false)
    private LocalDateTime updatedOn;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private Set<Address> addresses = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new LinkedHashSet<>();

    @PrePersist
    protected void onCreate() {
        status = UserStatus.INACTIVE;
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