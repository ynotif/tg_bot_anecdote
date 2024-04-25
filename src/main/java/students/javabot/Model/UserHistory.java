package students.javabot.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "userHistory")
@Table(name = "userHistory")
public class UserHistory {
    @Id
    @Column(name = "userHistoryId")
    @GeneratedValue(generator = "userHistory_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "userHistory_id_seq", sequenceName = "userHistory_id_seq", initialValue = 1, allocationSize = 1)
    private Long userHistoryId;

    @JoinColumn(name = "userId")
    @ManyToOne
    private User userId;

    @Column(name = "anecdoteId")
    private Long anecdoteId;

    @Column(name = "dateOfCalling")
    private Date dateOfCalling;

    @Column(name = "action")
    private String action;

}
