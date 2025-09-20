package sit.int204.itbmsbackend.entity;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_tracking")
@NoArgsConstructor
public class UserTracking {
    @Id
    private String id;
    private String userId;
    private String userFullName;
    private String action;
    private String timestamp;

    public UserTracking(String userId, String userFullName, String action, String timestamp) {
        this.userId = userId;
        this.userFullName = userFullName;
        this.action = action;
        this.timestamp = timestamp;
    }
}
