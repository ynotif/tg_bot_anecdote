package students.javabot.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AnecdoteNotFoundException extends Exception {

    private final Long id;

}
