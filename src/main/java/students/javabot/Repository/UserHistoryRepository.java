package students.javabot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import students.javabot.Model.UserHistory;
import java.util.*;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Integer> {

    List<UserHistory> getUserHistoryBy();

    Optional<UserHistory> findUserHistoriesByUserHistoryId(long id);

}
