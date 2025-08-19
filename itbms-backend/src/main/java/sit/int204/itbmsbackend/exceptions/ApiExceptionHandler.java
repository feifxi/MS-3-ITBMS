package sit.int204.itbmsbackend.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sit.int204.itbmsbackend.dtos.GeneralErrorResponseDto;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> handleSQLException(SQLIntegrityConstraintViolationException ex, HttpServletRequest request) {
        GeneralErrorResponseDto errorResponse =  new GeneralErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleKuyException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        GeneralErrorResponseDto errorResponse =  new GeneralErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error. Check 'errors' field for details.",
                request.getRequestURI()
        );
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            );
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<Map<String,String>> handleIllegalArg(IllegalArgumentException ex) {
//        return ResponseEntity.status(HttpStatus.CONFLICT)
//                .body(Map.of("error", ex.getMessage()));
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String,String>> handleValidation(MethodArgumentNotValidException ex) {
//        var first = ex.getBindingResult().getFieldErrors().stream().findFirst();
//        String msg = first.map(f -> f.getField() + " " + f.getDefaultMessage()).orElse("Invalid payload");
//        return ResponseEntity.badRequest().body(Map.of("error", msg));
//    }
}

