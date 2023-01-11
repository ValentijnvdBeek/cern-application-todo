package ch.cern.todo.repositories;

import ch.cern.todo.entities.Tasks;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface TasksRepository extends JpaRepository<Tasks, Long> {
    Optional<Tasks> findByTaskId(@NonNull Long taskId);

    List<Tasks> findByTaskNameIgnoreCase(@NonNull String taskName);

    @Override
    void deleteById(Long taskId);

    List<Tasks> findByDeadlineBefore(Timestamp deadline);

    List<Tasks> findByDeadlineAfter(@NonNull Timestamp deadline);

    List<Tasks> findByTaskCategory_CategoryNameIgnoreCase(String categoryName);

}
