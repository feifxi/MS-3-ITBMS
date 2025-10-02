package sit.int204.itbmsbackend.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderValidationResponse {
    private Boolean valid;
    private List<UpdatedCartItemResponse> updateItems;
}