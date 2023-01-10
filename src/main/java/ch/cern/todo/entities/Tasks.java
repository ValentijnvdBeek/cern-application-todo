package ch.cern.todo.entities;

import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.*;
import org.hibernate.Hibernate;

@Entity
@Table
public class Tasks {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "TASK_ID", nullable = false)
    private Long taskId;

    @Column(name = "TASK_NAME", nullable = false)
    private String taskName;

    @Column(name="TASK_DESCRIPTION")
    private String taskDescription;

    @ManyToOne(cascade = {CascadeType.REMOVE, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "task_category_id", nullable = false)
    private TaskCategory taskCategory;

    @Column(name = "deadline", nullable = false)
    private Timestamp deadline;

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public TaskCategory getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(TaskCategory taskCategory) {
        this.taskCategory = taskCategory;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Tasks tasks = (Tasks) o;
        return taskId != null && taskId.equals(tasks.taskId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
