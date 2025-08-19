package sit.int204.itbmsbackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sit.int204.itbmsbackend.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findOneByUsername(String username);
    Optional<User> findOneByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}