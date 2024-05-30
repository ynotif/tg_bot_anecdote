package students.javabot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import students.javabot.Model.UserRoles;

public interface UserRoleRepository extends JpaRepository<UserRoles, Long> {
}
