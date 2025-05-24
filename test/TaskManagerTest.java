import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.HistoryManager;
import com.yandex.taskManager.service.InMemoryHistoryManager;
import com.yandex.taskManager.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.testng.Assert.*;


abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected HistoryManager historyManager;

    protected Task createTask() {
        return new Task("имя задачи", "описание", Status.NEW, LocalDateTime.now(), 3);
    }

    protected Epic createEpic() {
        return new Epic("имя задачи", "описание", Status.NEW, LocalDateTime.now(), 3);
    }

    protected SubTask createSubTask(int epicId) {
        return new SubTask("Прочитать", "Книгу", Status.DONE, LocalDateTime.now(), 5, epicId);
    }

    protected abstract T createTaskManager();

    @BeforeEach
    public void setup() {
        taskManager = createTaskManager();
        historyManager = new InMemoryHistoryManager();

        assertNotNull(taskManager, "Менеджер задач не был создан");
    }

    @AfterEach
    public void cleanup() {
        if (taskManager != null) {
            taskManager.deleteAll();
        }
    }

    @Test
    public void shouldUpdateTask() {
        Task task = createTask();
        taskManager.addTask(task);

        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals(Status.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    public void shouldDeleteTask() {
        Task task = createTask();
        taskManager.addTask(task);

        taskManager.deleteTask();
        assertNull(taskManager.getTaskById(task.getId()), "Задача должна быть удалена");
    }

    @Test
    public void shouldGetAllTasks() {
        Task task1 = createTask();
        Task task2 = new Task("имя задачи", "описание", Status.NEW, LocalDateTime.now(), 3);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        List<Task> tasks = taskManager.getTasks();
        assertEquals(2, tasks.size(), "Неверное количество задач");
        assertTrue(tasks.contains(task1), "Первая задача отсутствует");
        assertTrue(tasks.contains(task2), "Вторая задача отсутствует");
    }

    @Test
    public void shouldHandleEmptyList() {
        assertTrue(taskManager.getTasks().isEmpty(), "Список должен быть пустым");
        assertTrue(taskManager.getEpics().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(taskManager.getSubTasks().isEmpty(), "Список подзадач должен быть пустым");
    }
}