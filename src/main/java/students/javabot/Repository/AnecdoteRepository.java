package students.javabot.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import students.javabot.Model.Anecdote;

import java.util.List;
import java.util.Optional;

public interface AnecdoteRepository extends JpaRepository<Anecdote, Long> {
    List<Anecdote> getAnecdoteBy();

    @Query("SELECT a FROM anecdote a WHERE a.anecdoteId = ?1")
    Optional<Anecdote> getAnecdoteById(long anecdoteId);

    @Query("SELECT a FROM anecdote a")
    Page<Anecdote> findAllWithPagination(Pageable pageable);
}
