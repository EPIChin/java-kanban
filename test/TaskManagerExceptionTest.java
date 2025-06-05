import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.InMemoryTaskManager;
import com.yandex.taskManager.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class TaskManagerExceptionTest {
    private final TaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void testAddOverlappingTasks() {
        Task task1 = new Task("имя задачи", "описание", Status.NEW, LocalDateTime.now(), 3);

        Assertions.assertDoesNotThrow(() -> taskManager.addTask(task1));

        Task task2 = new Task("имя задачи2", "описание2", Status.NEW, LocalDateTime.now(), 3);

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.addTask(task2));
        Assertions.assertEquals("Задача пересекается с существующими задачами", exception.getMessage());
    }

    @Test
    public void testGetNonExistentTaskById() {
        Assertions.assertDoesNotThrow(() -> taskManager.getTaskById(999));
    }

    @Test
    public void testDeleteNonExistentTask() {
        Assertions.assertDoesNotThrow(() -> taskManager.deleteTask(999));
    }
}
