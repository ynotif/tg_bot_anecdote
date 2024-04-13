package students.javabot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import students.javabot.Model.UserHistory;

import java.util.List;
import java.util.Optional;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {

    List<UserHistory> getUserHistoryBy();

    Optional<UserHistory> getUserHistoryById(Long id);

}

