package students.javabot.Model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "userEntity")
@Table(name = "userEntity")
@ToString
public class User implements UserDetails {
    @Id
    @Column(name = "userId")
    @GeneratedValue(generator = "userEntity_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "userEntity_id_seq", sequenceName = "userEntity_id_seq", initialValue = 1, allocationSize = 1)
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    private boolean expired;
    private boolean locked;
    private boolean enabled;

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserRoles> userRoles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // Права пользователя
        return userRoles.stream().map(UserRoles::getUserAuthority).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { // Аккаунт не истек
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() { // Заблокировать аккаунт
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() { // Пароль истекает раз в какое-то время
        return !expired;
    }

    @Override
    public boolean isEnabled() { // Включить выключить аккаунт
        return enabled;
    }
}
