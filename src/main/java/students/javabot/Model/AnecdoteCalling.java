package students.javabot.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "anecdoteCalling")
public class AnecdoteCalling {
    @Id
    @Column(name = "anecdoteCallingId")
    @GeneratedValue(generator = "anecdote_anecdoteCallingId_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "anecdote_anecdoteCallingId_seq", sequenceName = "anecdote_anecdoteCallingId_seq", initialValue = 1, allocationSize = 1)
    private long anecdoteCallingId;

}
