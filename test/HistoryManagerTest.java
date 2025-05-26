import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.HistoryManager;
import com.yandex.taskManager.service.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void historyManagerAdd() {
        historyManager.add(new Task("починть машину", "важно"));
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    protected Task createTask() {
        return new Task("TaskName", "TaskDescription", Status.NEW);
    }

    protected Epic createEpic() {
        return new Epic("EpicName", "EpicDescription");
    }

    protected SubTask createSubTask(int epicId) {
        return new SubTask("Прочитать", "Книгу", Status.DONE, epicId);
    }

    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        task1 = new Task("имя задачи", "описание", Status.NEW, LocalDateTime.now(), 3);
        task1.setId(5);
        task2 = new Task("имя задачи2", "описание2", Status.NEW, LocalDateTime.now().plusMinutes(10), 4);
        task2.setId(6);
        task3 = new Task("имя задачи3", "описание3", Status.NEW, LocalDateTime.now().plusMinutes(333), 5);
        task3.setId(7);
    }


    @Test
    public void addTasksToHistoryAndNotDuplicate() {
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.add(task1);
        historyManager.getHistory();
        assertEquals(List.of(task1), historyManager.getHistory());
    }

    @Test
    public void addEpicsToHistoryAndNotDuplicate() {
        Epic epic1 = createEpic();
        Epic epic2 = createEpic();
        Epic epic3 = createEpic();
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(epic3);
        assertEquals(List.of(epic3), historyManager.getHistory());
    }

    @Test
    public void addSubTaskAndSubTaskToHistory() {
        Epic epic = createEpic();
        SubTask subTask = createSubTask(0);
        subTask.setId(1);
        historyManager.add(epic);
        historyManager.add(subTask);
        assertEquals(List.of(epic, subTask), historyManager.getHistory());
    }

    @Test
    public void removeOnlyOneTask() {
        Task task = createTask();
        historyManager.add(task);
        historyManager.remove(task.getId());
        assertEquals(Collections.emptyList(), historyManager.getHistory());
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(Collections.emptyList(), historyManager.getHistory());
    }

    @Test
    void testEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void testAddTask() {
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
    }

    @Test
    void testAddMultipleTasks() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(3, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task2, historyManager.getHistory().get(1));
        assertEquals(task3, historyManager.getHistory().get(2));
    }

    @Test
    void testRemoveFromStart() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(5);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task2, historyManager.getHistory().get(0));
        assertEquals(task3, historyManager.getHistory().get(1));
    }

    @Test
    void testRemoveFromMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(6);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task3, historyManager.getHistory().get(1));
    }

    @Test
    void testRemoveFromEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(7);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task2, historyManager.getHistory().get(1));
    }

    @Test
    void testDuplicateTask() {
        historyManager.add(task1);
        historyManager.add(task1);

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
    }

    @Test
    void testNullTask() {
        assertThrows(IllegalArgumentException.class, () -> historyManager.add(null));
    }
}
