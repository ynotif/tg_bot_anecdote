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
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


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
                "id=" + id +
                ", text='" + text + '\'' +
                ", dateOfCreation=" + dateOfCreation +
                ", dateOfUpdate=" + dateOfUpdate +
                ", registeredAt=" + registeredAt +
                '}';
    }
}
