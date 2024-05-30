package students.javabot.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import students.javabot.Model.Anecdote;
import students.javabot.Repository.AnecdoteRepository;

@Service
@RequiredArgsConstructor
public class CreateAnecdoteServiceImpl implements CreateAnecdoteService {

    private final AnecdoteRepository anecdoteRepository;

    @Override
    public Anecdote createAnecdote(Anecdote anecdote) {
        return anecdoteRepository.save(anecdote);
    }
}
