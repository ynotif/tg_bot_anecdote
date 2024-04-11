package students.javabot.Model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "anecdote")
@Table(name = "anecdote")
public class Anecdote {
    @Id
    @Column(name = "userId")
    @GeneratedValue(generator = "anecdote_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "anecdote_id_seq", sequenceName = "anecdote_id_seq", initialValue = 1, allocationSize = 1)
    private Long userId;


    @Column(name = "text")
    private String text;


    @Column(name = "date_of_creation")
    private Date dateOfCreation;

    @Column(name = "date_of_update")
    private Date dateOfUpdate;

    @Column(name = "date_of_register")
    private String registeredAt;

    @Override
    public String toString() {
        return "Anecdote{" +
                "id=" + userId +
                ", text='" + text + '\'' +
                ", dateOfCreation=" + dateOfCreation +
                ", dateOfUpdate=" + dateOfUpdate +
                ", registeredAt=" + registeredAt +
                '}';
    }
}
