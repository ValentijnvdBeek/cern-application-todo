package ch.cern.todo.controllers;

import ch.cern.todo.entities.TaskCategory;
import ch.cern.todo.entities.Tasks;
import ch.cern.todo.errors.BadRequestException;
import ch.cern.todo.errors.NotFoundException;
import ch.cern.todo.repositories.TaskCategoryRepository;

import ch.cern.todo.repositories.TasksRepository;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;


@RestController
public class TodoController {
    @Autowired
    private transient TasksRepository tasksRepository;

    @Autowired
    private transient TaskCategoryRepository taskCategoryRepository;

    static final String prelude = "/v1";

    private TaskCategory getTaskCategory(Long id) throws NotFoundException {
        // Fetch the category based on just the ID and then get it out of the database.
        Optional<TaskCategory> tc = taskCategoryRepository.findById(id);
        if (tc.isEmpty()) {
            throw new NotFoundException("Cannot find category " + id);
        }
        return tc.get();
    }

    /**
     * Gets all the tasks from the system and returns it as a JSON list.
     * By default, it sorts each task by descending by time, but this can be configured.
     * It also supports filtering by name to narrow the search area.
     *
     * @param name Task name that the returned should be filtered
     *     on. If left empty, it will return all tasks
     * @param sort Can either be naming or time. If set to name, it will sort
     *     the tasks alphabetically
     * @param direction Either descending or descending.
     *
     * @return All tasks matching the invariants
     * @see Tasks
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @GetMapping(prelude + "/tasks")
    public List<Tasks> getTasks(@RequestParam(required=false, name="name") String name,
                                @RequestParam(required=false, name="category") String category,
                                @RequestParam(required=false, name="sort") String sort,
                                @RequestParam(required=false, name="direction") String direction) {
        List<Tasks> tasks;
        if (name != null) {
            tasks = tasksRepository.findByTaskNameIgnoreCase(name);
        } else {
            tasks = tasksRepository.findAll();
        }

        if (category != null) {
            tasks = tasks.stream()
                .filter(task -> task.getTaskCategory().getCategoryName().equals(category))
                .collect(Collectors.toList());
        }

        // By default, we sort time ascending
        // False positive: DD-anomaly
        Comparator<Tasks> comp = Comparator.comparing(Tasks::getDeadline,
                                                      Comparator.naturalOrder());
        if (sort != null && sort.equals("name")) {
            comp = Comparator.comparing(Tasks::getTaskName, String::compareToIgnoreCase)
                .reversed();
        }

        if (direction != null && (direction.equals("desc") || direction.equals("descending"))) {
            comp = comp.reversed();
        }

        tasks.sort(comp);

        return tasks;
    }

    /**
     * Gets a particular task based on its id.
     * @return Task associated with the id.
     * @see Tasks
     */
    @GetMapping(prelude + "/tasks/{id}")
    public Tasks getTask(@PathVariable(name="id") Long id)
        throws BadRequestException, NotFoundException {
        if (id == null) {
            throw new BadRequestException("id cannot be nil");
        }

        Optional<Tasks> task = tasksRepository.findByTaskId(id);
        if (task.isEmpty()) {
            throw new NotFoundException("Cannot find task with id " + id);
        }

        return task.get();
    }

    /**
     * Create a TODO in the system. Every task should have at least have a category object
     * with an id parameter, taskName and deadline.
     *
     * @param task JSON representation of a task.
     * @see Tasks
     */
    @PostMapping(prelude + "/tasks")
    public Tasks addTask(@RequestBody Tasks task) throws BadRequestException, NotFoundException {
        // Ensure the state of the input actually valid.
        if (task == null) {
            throw new BadRequestException("No task provided.");
        } else if (task.getDeadline() == null || task.getTaskCategory() == null
                   || task.getTaskName() == null) {
            throw new BadRequestException("Deadline, task category and task name cannot be null");
        }

        TaskCategory category = this.getTaskCategory(task.getTaskCategory().getId());
        task.setTaskCategory(category);

        tasksRepository.save(task);
        Optional<Tasks> t = tasksRepository.findByTaskId(task.getTaskId());
        if (t.isEmpty()) {
            throw new BadRequestException("Failed to create new TODO task.");
        }

        return t.get();
    }

    /**
     * Safely updates a task identified by its ids. This method takes
     * the json representation with the requested changes and
     * preserves the prior-contents.
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @PutMapping(prelude + "/tasks")
    public Tasks updateTask(@RequestBody Tasks updatedTask)
        throws BadRequestException, NotFoundException {
        if (updatedTask.getTaskId() == null) {
            throw new BadRequestException("id cannot be null");
        }

        Optional<Tasks> taskOptional = tasksRepository.findByTaskId(updatedTask.getTaskId());
        if (taskOptional.isEmpty()) {
            throw new NotFoundException("Cannot find task with id " + updatedTask.getTaskId());
        }

        // False positive DU
        Tasks task = taskOptional.get();
        if (updatedTask.getTaskCategory() != null) {
            TaskCategory category = getTaskCategory(updatedTask.getTaskCategory().getId());
            updatedTask.setTaskCategory(category);
        }

        if (updatedTask.getTaskName() == null) {
            updatedTask.setTaskName(task.getTaskName());
        }

        if (updatedTask.getTaskDescription() == null) {
            updatedTask.setTaskDescription(task.getTaskDescription());
        }

        tasksRepository.save(updatedTask);
        return updatedTask;
    }

    /**
     * Returns all tasks that happen before or after a specific
     * time. If the deadline is not set, it will use the current system time.
     *
     * @param when Either before or after.
     * @param time A date formatted in the form: YY-mm-dd HH:mm:ss
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @GetMapping(prelude + "/tasks/deadline/{when}")
    public List<Tasks> getTasksDeadline(@PathVariable(name="when") String when,
                                        @RequestParam(name="time", required=false) String time) {
        Timestamp timestamp = (time != null) ? Timestamp.valueOf(time) :
            new Timestamp(System.currentTimeMillis());

        if (when.equals("before")) {
            return tasksRepository.findByDeadlineBefore(timestamp);
        } else if (when.equals("after")) {
            return tasksRepository.findByDeadlineAfter(timestamp);
        }
        return tasksRepository.findAll();
    }

    /**
     * Deletes a task from the database.
     */
    @DeleteMapping(prelude + "/tasks/{id}")
    @Transactional
    public String deleteTaskById(@PathVariable("id") Long id) throws NotFoundException {
        String msg;
        try {
            tasksRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException("Task " + id + " cannot be found");
        }
        return "Successfully deleted task: " + id;
    }

}
