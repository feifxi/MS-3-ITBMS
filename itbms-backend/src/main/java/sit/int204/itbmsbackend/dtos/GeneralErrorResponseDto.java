package sit.int204.itbmsbackend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneralErrorResponseDto {
    private final int status;
    private final String message;
    private final String instance;
    private Instant timestamp = Instant.now();
    private String stackTrace;
    private List<ValidationError> errors = new ArrayList<>();;

    // Nested class using only in this class
    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class ValidationError {
        private final String field;
        private final String message;
    }
    public void addValidationError(String field, String message){
        errors.add(new ValidationError(field, message));
    }
}
