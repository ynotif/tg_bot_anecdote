package students.javabot.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import students.javabot.Model.User;
import students.javabot.Model.UserAuthority;
import students.javabot.Model.UserRoles;
import students.javabot.Repository.UserRepository;
import students.javabot.Repository.UserRoleRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final UserRoleRepository usersRolesRepository;

    private final PasswordEncoder passwordEncoder;

    public void registration(String username, String password) throws Exception {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = userRepository.save(
                    new User()
                            .setUserId(null)
                            .setUsername(username)
                            .setPassword(passwordEncoder.encode(password))
                            .setLocked(false)
                            .setExpired(false)
                            .setEnabled(true)
            );
            usersRolesRepository.save(new UserRoles(null, UserAuthority.USER, user));
        } else {
            throw new Exception();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
