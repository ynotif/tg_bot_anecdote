package students.javabot.Service;

import org.springframework.stereotype.Service;
import students.javabot.Model.Anecdote;
import students.javabot.Repository.AnecdoteRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AnecdoteServiceHttp implements AnecdoteService{
    private AnecdoteRepository anecdoteRepository;
    @Override
    public Anecdote registerAnecdote(Anecdote anecdote){
        Date date = new Date();
        anecdote.setDateOfCreation(date);
        anecdote.setDateOfUpdate(null);
        return anecdoteRepository.save(anecdote);
    }

    @Override
    public List<Anecdote> getAllAnecdotes(){
        return anecdoteRepository.getAnecdoteBy();
    }

    @Override
    public Optional<Anecdote> getAnecdoteById(Long id){
        return anecdoteRepository.getAnecdoteById(id);
    }

    @Override
    public Anecdote updateAnecdoteById(Long id, Anecdote anecdote) {
        Date date = new Date();
        Anecdote updateAnecdote = anecdoteRepository.getAnecdoteById(id).orElseThrow();
        updateAnecdote.setText(anecdote.getText());
        updateAnecdote.setDateOfUpdate(date);
        return anecdoteRepository.save(updateAnecdote);
    }

    @Override
    public void deleteAnecdoteById(Long id){
        Anecdote deleteAnecdote = anecdoteRepository.getAnecdoteById(id).orElseThrow();
        anecdoteRepository.delete(deleteAnecdote);
    }
}
