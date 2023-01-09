package ch.cern.todo.repositories;

import ch.cern.todo.entities.TaskCategory;
import ch.cern.todo.entities.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface TasksRepository extends JpaRepository<Tasks, Integer> {
    Optional<Tasks> findByTaskId(@NonNull int taskId);

    List<Tasks> findByTaskNameIgnoreCase(@NonNull String taskName);

    @Transactional
    @Modifying
    @Query("update Tasks t set t.taskId = :taskId1, t.taskName = :taskName, t.taskDescription = :taskDescription, t.taskCategory = :taskCategory, t.deadline = :deadline " +
            "where t.taskId = :taskId6")
    int updateTaskById(@Param("taskId") int taskId, @Param("taskName") String taskName, @Param("taskDescription") String taskDescription, @Param("taskCategory") TaskCategory taskCategory, @Param("deadline") Timestamp deadline, @Param("taskId") int taskId1);

    long deleteByTaskId(@NonNull int taskId);

    List<Tasks> findByDeadlineBefore(Timestamp deadline);

    List<Tasks> findByDeadlineAfter(@NonNull Timestamp deadline);

    List<Tasks> findByTaskCategory_CategoryNameIgnoreCase(String categoryName);

}