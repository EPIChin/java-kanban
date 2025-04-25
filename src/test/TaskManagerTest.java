package test;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.InMemoryTaskManager;
import com.yandex.taskManager.service.Managers;
import com.yandex.taskManager.service.TaskManager;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskManagerTest {
    protected final TaskManager taskManager = Managers.getDefault();

    protected Task createTask() {
        return new Task("TaskName", "TaskDescription", Status.NEW);
    }

    protected Epic createEpic() {
        return new Epic("EpicName", "EpicDescription");
    }

    protected SubTask createSubTask(int epicId) {
        return new SubTask("Прочитать", "Книгу", Status.DONE, epicId);
    }

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
    @Test
    public void UpdateTaskStatusToInProgress() {
        Task task = createTask();
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(task.getId()).getStatus());
    }

    @Test
    public void UpdateEpicStatusToInProgress() {
        Epic epic = createEpic();
        epic.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(epic);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(epic.getId()).getStatus());
    }

    @Test
    public void DeleteAll(){
        Task task = createTask();
        Epic epic = createEpic();
        SubTask subTask = createSubTask(1);
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.deleteAll();
        assertEquals(Collections.emptyList(), taskManager.getTasks());
        assertEquals(Collections.emptyList(), taskManager.getEpics());
        assertEquals(Collections.emptyList(), taskManager.getSubTasks());
    }

    @Test
    public void DeleteTaskById() {
        Task task = createTask();
        taskManager.deleteTask(task.getId());
        assertEquals(Collections.emptyList(), taskManager.getTasks());
    }

    @Test
    public void SubtasksNotShouldStoreOldId() {
        Epic epic = createEpic();
        SubTask subTask = createSubTask(0);
        SubTask subTask2 = createSubTask(0);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);
        taskManager.deleteSubTask(subTask.getId());
        assertEquals(taskManager.getAllSubtasksOfEpic(0), taskManager.getSubTasks());

    }
}