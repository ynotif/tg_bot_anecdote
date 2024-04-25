package students.javabot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import students.javabot.Model.User;

import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> getUserBy();

    Optional<User> findByUserName(String userName);

    Optional<User> getUserByUserId(Long id);
}