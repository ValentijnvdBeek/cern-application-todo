package ch.cern.todo.repositories;

import ch.cern.todo.entities.TaskCategory;
import ch.cern.todo.entities.Tasks;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface TasksRepository extends JpaRepository<Tasks, Integer> {
    Optional<Tasks> findByTaskId(@NonNull Long taskId);

    List<Tasks> findByTaskNameIgnoreCase(@NonNull String taskName);

    long deleteByTaskId(@NonNull int taskId);

    List<Tasks> findByDeadlineBefore(Timestamp deadline);

    List<Tasks> findByDeadlineAfter(@NonNull Timestamp deadline);

    List<Tasks> findByTaskCategory_CategoryNameIgnoreCase(String categoryName);

}
