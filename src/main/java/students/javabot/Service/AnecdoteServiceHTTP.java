package students.javabot.Service;

import students.javabot.Model.Anecdote;
import students.javabot.exceptions.AnecdoteNotFoundException;

import java.util.List;
import java.util.Optional;

public interface AnecdoteServiceHTTP {

    Anecdote createAnecdote(Anecdote anecdote);

    List<Anecdote> getAllAnecdotes();

    Optional<Anecdote> getAnecdoteById(Long id) throws AnecdoteNotFoundException;

    void deleteAnecdoteById(Long id) throws AnecdoteNotFoundException;

    Optional<Anecdote> updateAnecdoteById(Long id, Anecdote anecdote) throws AnecdoteNotFoundException;
}
