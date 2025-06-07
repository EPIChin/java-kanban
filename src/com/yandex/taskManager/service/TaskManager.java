package com.yandex.taskManager.service;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubTasks();

    void deleteTask();

    void deleteEpics();

    void deleteSubTask();

    Task getTaskById(int id);

    Task getEpicById(int id);

    Task getSubTaskById(int id);

    void updateTask(Task task);

    void updateEpics(Epic epic);

    void updateSubTask(SubTask subTask);

    void deleteTask(int id);

    void deleteEpics(int id);

    void deleteSubTask(int id);

    ArrayList<SubTask> getAllSubtasksOfEpic(int id);

    Status checkStatusEpic(SubTask subTask);

    void deleteAll();

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();


}
