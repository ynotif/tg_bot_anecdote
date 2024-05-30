package students.javabot.Model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@Entity
@Table(name = "userRoles")
@NoArgsConstructor
@AllArgsConstructor
public class UserRoles {

    @Id
    @GeneratedValue(generator = "userRole_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "userRole_id_seq", sequenceName = "userRole_id_seq", allocationSize = 1, initialValue = 1)
    @Column(name = "userRoleId")
    private Long userRoleId;

    @Enumerated // помечаем т.к. enum
    private UserAuthority userAuthority;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

}
