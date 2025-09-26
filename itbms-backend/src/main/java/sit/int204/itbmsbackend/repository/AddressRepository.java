package sit.int204.itbmsbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sit.int204.itbmsbackend.entity.Address;
import sit.int204.itbmsbackend.entity.Brand;
import sit.int204.itbmsbackend.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer>  {
    Optional<Address> findFirstByUser(User user);
}
