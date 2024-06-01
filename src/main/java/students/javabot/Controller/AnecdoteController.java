package students.javabot.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import students.javabot.Model.Anecdote;
import students.javabot.Service.AnecdoteServiceHTTP;
import students.javabot.exceptions.AnecdoteNotFoundException;

import java.util.List;
import java.util.Optional;

@RequestMapping("/anecdote")
@RestController
@RequiredArgsConstructor
public class AnecdoteController {

    private final AnecdoteServiceHTTP anecdoteServiceHTTP;

    @PostMapping()
    public ResponseEntity<Anecdote> createAnecdote(@RequestBody Anecdote anecdote) {
        return ResponseEntity.ok(anecdoteServiceHTTP.createAnecdote(anecdote));
    }

    @GetMapping()
    public ResponseEntity<List<Anecdote>> getAnecdotes() {
        return ResponseEntity.ok(anecdoteServiceHTTP.getAllAnecdotes());
    }

    @GetMapping("/{anecdoteId}")
    public ResponseEntity<Optional<Anecdote>> getAnecdoteById(@PathVariable Long anecdoteId) throws AnecdoteNotFoundException {
        return ResponseEntity.ok(anecdoteServiceHTTP.getAnecdoteById(anecdoteId));
    }

    @PutMapping("/{anecdoteId}")
    public ResponseEntity<Optional<Anecdote>> updateAnecdoteById(@RequestBody Anecdote newAnecdote, @PathVariable Long anecdoteId) throws AnecdoteNotFoundException {
        return ResponseEntity.ok(anecdoteServiceHTTP.updateAnecdoteById(anecdoteId, newAnecdote));
    }

    @DeleteMapping("/{anecdoteId}")
    public ResponseEntity<String> deleteAnecdoteById(@PathVariable Long anecdoteId) throws AnecdoteNotFoundException {
        anecdoteServiceHTTP.deleteAnecdoteById(anecdoteId);
        return ResponseEntity.ok("Anecdote delete successfully!");
    }
}
