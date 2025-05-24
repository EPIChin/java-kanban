import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.FileBackedTaskManager;
import org.junit.jupiter.api.*;

import java.io.*;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest {

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks-test", ".csv");
        tempFile.deleteOnExit();
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    private File tempFile;

    @Test
    void createAndLoadTask() throws Exception {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Задача", "Описание", Status.NEW, LocalDateTime.now(), 5);
        manager.addTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Assertions.assertEquals(1, loadedManager.getTasks().size());
        Assertions.assertEquals(task, loadedManager.getTasks().get(0));
    }

    @Test
    void emptyFile() {
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);
        Assertions.assertTrue(manager.getTasks().isEmpty());
        Assertions.assertTrue(manager.getEpics().isEmpty());
        Assertions.assertTrue(manager.getSubTasks().isEmpty());
    }

    @Test
    void saveMultipleTasks() throws Exception {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Задача 1", "Описание 1", Status.NEW, LocalDateTime.now(), 5);
        Task task2 = new Task("Задача 2", "Описание 2", Status.DONE, LocalDateTime.now().plusMinutes(200), 5);
        manager.addTask(task1);
        manager.addTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Assertions.assertEquals(2, loadedManager.getTasks().size());
        Assertions.assertEquals(task1, loadedManager.getTasks().get(0));
        Assertions.assertEquals(task2, loadedManager.getTasks().get(1));
    }

    @Test
    void saveAndLoadEpic() throws Exception {
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        manager.addEpic(epic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Assertions.assertEquals(1, loadedManager.getEpics().size());
        Assertions.assertEquals(epic, loadedManager.getEpics().get(0));
    }

    @Test
    void saveAndLoadSubTask() throws Exception {
        // Arrange
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Epic epic = new Epic("Эпик", "Описание эпика", Status.IN_PROGRESS);
        manager.addEpic(epic);
        SubTask subTask = new SubTask("Подзадача", "Описание подзадачи",
                Status.NEW, LocalDateTime.now(), 5, epic.getId());
        manager.addSubTask(subTask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Assertions.assertEquals(1, loadedManager.getEpics().size());
        Assertions.assertEquals(1, loadedManager.getSubTasks().size());
        Assertions.assertEquals(epic, loadedManager.getEpics().get(0));
        Assertions.assertEquals(subTask, loadedManager.getSubTasks().get(0));
    }
}