package students.javabot.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Entity(name = "userHistory")
@Table(name = "userHistory")
public class UserHistory {
    @Id
    @Column(name = "userHistoryId")
    @GeneratedValue(generator = "userHistoryId_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "userHistoryId_seq", sequenceName = "userHistoryId_seq", initialValue = 1, allocationSize = 1)
    private long id;

    @JsonIgnore
    @JoinColumn(name = "anecdoteId")
    @ManyToOne
    private Anecdote anecdote;

    @Column(name = "dateCalling")
    private Date dateCalling;
}
