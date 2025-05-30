package com.devtiro.tasks.services.impl;

import com.devtiro.tasks.domain.entities.Task;
import com.devtiro.tasks.domain.entities.TaskList;
import com.devtiro.tasks.domain.entities.TaskPriority;
import com.devtiro.tasks.domain.entities.TaskStatus;
import com.devtiro.tasks.repositories.TaskListRepository;
import com.devtiro.tasks.repositories.TaskRepository;
import com.devtiro.tasks.services.TaskListService;
import com.devtiro.tasks.services.TaskService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;

    public TaskServiceImpl(TaskRepository taskRepository, TaskListRepository taskListRepository) {
        this.taskRepository = taskRepository;
        this.taskListRepository = taskListRepository;
    }


    @Override
    public List<Task> listTasks(UUID taskListId) {
        return taskRepository.findByTaskListId(taskListId);
    }

    @Transactional
    @Override
    public Task createTask(UUID taskListId, Task task) {
        if (null != task.getId()) {
            throw new IllegalArgumentException("Task already has ID!");
        }
        if (null == task.getTitle() || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task must have a title");
        }
        TaskPriority priority = Optional.ofNullable(task.getPriority()).orElse(TaskPriority.MEDIUM);
        TaskList taskList = taskListRepository.findById(taskListId).orElseThrow(() -> new IllegalArgumentException("Invalid Task List ID provided"));
        LocalDateTime now = LocalDateTime.now();
        return taskRepository.save(new Task(null, task.getTitle(), task.getDescription(), task.getDueDate(), TaskStatus.OPEN, priority, taskList, now, now));
    }

    @Override
    public Optional<Task> getTask(UUID taskListId, UUID taskId) {
        return taskRepository.findByTaskListIdAndId(taskListId, taskId);
    }


    @Transactional
    @Override
    public Task updateTask(UUID taskListId, UUID taskId, Task task) {
        if (null == task.getId()) {
            throw new IllegalArgumentException("Task must have ID!");
        }
        if (!Objects.equals(taskId, task.getId())) {
            throw new IllegalArgumentException("Task IDs do not match!");
        }
        if (null == task.getPriority()) {
            throw new IllegalArgumentException("Task must have a valid priority!");
        }
        if (null == task.getStatus()) {
            throw new IllegalArgumentException("Task must have a valid status!");
        }
        Task existingTask = taskRepository.findByTaskListIdAndId(taskListId, task.getId()).orElseThrow(() -> new IllegalStateException("Task not found!"));
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDueDate(task.getDueDate());
        existingTask.setPriority(task.getPriority());
        existingTask.setStatus(task.getStatus());
        existingTask.setUpdated(LocalDateTime.now());
        return taskRepository.save(existingTask);
    }


    @Transactional  // need to use this since we are using custom delete method in jpa| for default it's there already.
    @Override
    public void deleteTask(UUID taskListId, UUID taskId) {
        taskRepository.deleteByTaskListIdAndId(taskListId, taskId);
    }
}
