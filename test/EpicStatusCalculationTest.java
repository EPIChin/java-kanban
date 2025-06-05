import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicStatusCalculationTest {

    private InMemoryTaskManager taskManager;
    private Epic epic;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();

        epic = new Epic("имя задачи", "описание", Status.NEW, LocalDateTime.now(), 3);
        taskManager.addEpic(epic);

        subTask1 = new SubTask("Прочитать", "Книгу", Status.NEW, LocalDateTime.now().plusDays(1), 5, epic.getId());

        subTask2 = new SubTask("Прочитать2", "Книгу2", Status.NEW, LocalDateTime.now().plusDays(2), 5, epic.getId());
    }

    @Test
    void shouldHaveNEWStatus_WhenAllSubtasksAreNEW() {
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldHaveDONEStatus_WhenAllSubtasksAreDONE() {
        // Добавляем подзадачи и меняем их статус на DONE
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void shouldHaveIN_PROGRESSStatus_WhenMixedSubtasks() {
        // Добавляем подзадачи с разными статусами
        taskManager.addSubTask(subTask1);  // Статус NEW
        subTask2.setStatus(Status.DONE);
        taskManager.addSubTask(subTask2);  // Статус DONE

        // Проверяем статус эпика
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldHaveIN_PROGRESSStatus_WhenSubtasksAreInProgress() {
        // Добавляем подзадачи и меняем их статус на IN_PROGRESS
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        // Проверяем статус эпика
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}
