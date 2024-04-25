package students.javabot.Service;

import students.javabot.Model.Anecdote;

import java.util.List;
import java.util.Optional;

public interface AnecdoteService {
    Anecdote registerAnecdote(Anecdote anecdote);

    List<Anecdote> getAllAnecdotes();

    Optional<Anecdote> getAnecdoteById(Long anecdoteId);

    Anecdote updateAnecdoteById(Long anecdoteId, Anecdote anecdote);

    void deleteAnecdoteById(Long anecdoteId);
}