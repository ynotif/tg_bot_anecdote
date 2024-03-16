package students.javabot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import students.javabot.Model.Anecdote;
import java.util.List;
import java.util.Optional;

public interface AnecdoteRepository extends JpaRepository<Anecdote, Long> {
    List<Anecdote> getAnecdoteBy();

    Optional<Anecdote> getAnecdoteById(Long id);
}
