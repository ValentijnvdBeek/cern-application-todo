package ch.cern.todo.repositories;

import ch.cern.todo.entities.TaskCategory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Long> {
    @Override
    Optional<TaskCategory> findById(Long along);

    @Override
    void deleteById(Long along);

    @Query("select t from TaskCategory t where t.categoryName = :categoryName")
    TaskCategory findByCategoryName(@Param("categoryName") String categoryName);
}
