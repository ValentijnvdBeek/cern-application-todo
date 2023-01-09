package ch.cern.todo.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table
public class Tasks {
	@Id
	@Column(name="TASK_ID")
	private int taskId;

	@Column(name = "TASK_NAME", nullable = false)
	private String taskName;

	@Column(name="TASK_DESCRIPTION")
	private String taskDescription;

	@Column(name="PASSWORD")
	private String password;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
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

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
