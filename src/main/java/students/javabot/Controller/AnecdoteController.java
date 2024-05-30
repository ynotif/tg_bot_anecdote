package students.javabot.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import students.javabot.Model.Anecdote;
import students.javabot.Service.CreateAnecdoteService;

@RequestMapping("/anecdote")
@RestController
@RequiredArgsConstructor
public class AnecdoteController {

    private final CreateAnecdoteService createAnecdoteService;

    @PostMapping()
    public ResponseEntity<Anecdote> createAnecdote(Anecdote anecdote) {
        createAnecdoteService.createAnecdote(anecdote);
        return ResponseEntity.ok(anecdote);
    }
}
