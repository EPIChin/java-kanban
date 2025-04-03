package com.yandex.taskManager.test;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.InMemoryTaskManager;
import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    protected final TaskManager taskManager = Managers.getDefault();

    @Test
    void addNewTask() {
        Task task = new Task("починть машину", "важно");
        taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(0);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Уборка!", "пора");
        taskManager.addEpic(epic);

        final Task savedEpic = taskManager.getEpicById(0);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Уборка!", "пора");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Уборка в кухне", "помыть посуду", Status.IN_PROGRESS, 0);
        taskManager.addSubTask(subTask);

        final Task savedSubTask = taskManager.getSubTaskById(1);

        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask, savedSubTask, "Задачи не совпадают.");

        final List<SubTask> SubTasks = taskManager.getSubTasks();

        assertNotNull(SubTasks, "Задачи не возвращаются.");
        assertEquals(1, SubTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, SubTasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void utilityClassReturnsInitializedManagers() {
        TaskManager defaultTaskManager = Managers.getDefault();
        TaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task("починть машину", "важно");
        taskManager.addTask(task);
        defaultTaskManager.addTask(task);

        final Task savedTask1 = taskManager.getTaskById(0);
        final Task savedTaskDefault = defaultTaskManager.getTaskById(0);

        assertEquals( savedTask1, savedTaskDefault, "Задачи не совпадают.");
    }
}