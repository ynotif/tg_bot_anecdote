package students.javabot.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class AnecdoteExceptionHandler {

    @ExceptionHandler(AnecdoteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAnecdoteNotFoundException(AnecdoteNotFoundException e) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        400L,
                        "Нет анекдота с таким id: " + e.getId()
                )
        );
    }

}
