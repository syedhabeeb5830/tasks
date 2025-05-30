package com.devtiro.tasks.controllers;


import com.devtiro.tasks.domain.dto.TaskDto;
import com.devtiro.tasks.domain.dto.TaskListDto;
import com.devtiro.tasks.domain.entities.Task;
import com.devtiro.tasks.mappers.TaskMapper;
import com.devtiro.tasks.services.impl.TaskServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/task-lists/{task_list_id}/tasks")
public class TaskController {
    private final TaskServiceImpl taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskServiceImpl taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }


    @GetMapping
    public List<TaskDto> listTasks(@PathVariable("task_list_id") UUID taskListId){
        return taskService.listTasks(taskListId).stream().map(taskMapper::toDto).toList();
    }

    @PostMapping
    public TaskDto createTask(@PathVariable("task_list_id") UUID taskListId, @RequestBody TaskDto taskDto) {
        Task createdTask = taskService.createTask(taskListId, taskMapper.fromDto(taskDto));
        return taskMapper.toDto(createdTask);
    }

    @GetMapping("/{task_id}")
    public Optional<TaskDto> getTask(@PathVariable("task_list_id") UUID taskListId, @PathVariable("task_id") UUID taskId) {
        return taskService.getTask(taskListId, taskId).map(taskMapper::toDto);
    }


    @PutMapping("/{task_id}")
    public TaskDto updateTask(@PathVariable("task_list_id") UUID taskListId, @PathVariable("task_id") UUID taskId, @RequestBody TaskDto taskDto){
        return taskMapper.toDto(taskService.updateTask(taskListId, taskId, taskMapper.fromDto(taskDto)));
    }

    @DeleteMapping("/{task_id}")
    public void deleteTask(@PathVariable("task_list_id") UUID taskListId, @PathVariable("task_id") UUID taskId){
        taskService.deleteTask(taskListId, taskId);
    }
}
