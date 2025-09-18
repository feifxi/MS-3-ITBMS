package sit.int204.itbmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sit.int204.itbmsbackend.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findOneByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<User> findOneByVerificationToken(String verificationToken);

    @Query("SELECT u FROM User u WHERE u.verificationTokenExpiry < :now")
    List<User> findAllByExpiresToken(@Param("now") LocalDateTime now);

    @Query("SELECT u FROM User u WHERE u.verificationToken = :token AND u.verificationTokenExpiry > :now")
    Optional<User> findByVerificationTokenUnExpires(@Param("token") String token, @Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM User u WHERE u.verificationTokenExpiry < :now")
    void deleteNonVerifiedUsers(@Param("now") LocalDateTime now);
}