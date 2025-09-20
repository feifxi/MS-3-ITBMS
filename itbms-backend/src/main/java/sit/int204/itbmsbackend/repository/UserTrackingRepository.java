package sit.int204.itbmsbackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sit.int204.itbmsbackend.entity.UserTracking;

public interface UserTrackingRepository extends MongoRepository<UserTracking, String> {
    // custom queries if needed
}
