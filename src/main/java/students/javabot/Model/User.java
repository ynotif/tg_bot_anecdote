package students.javabot.Model;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "userEntity")
@Table(name = "userEntity")
public class User {
    @Id
    @Column(name = "userId")
    @GeneratedValue(generator = "userEntity_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "userEntity_id_seq", sequenceName = "userEntity_id_seq", initialValue = 1, allocationSize = 1)
    private Long userId;

    @Column(name = "userName")
    private String userName;
}
