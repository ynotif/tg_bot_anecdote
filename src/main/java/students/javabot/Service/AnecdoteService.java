package students.javabot.Service;
import java.util.List;
import java.util.Optional;

import students.javabot.Model.Anecdote;

public interface AnecdoteService {
    Anecdote registerAnecdote(Anecdote anecdote);

    List<Anecdote> getAllAnecdotes();

    Optional<Anecdote> getAnecdoteById(Long id);

    Anecdote updateAnecdoteById(Long id, Anecdote anecdote);

    void deleteAnecdoteById(Long id);
}
