import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.HistoryManager;
import com.yandex.taskManager.service.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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


    @Test
    public void addTasksToHistoryAndNotDuplicate() {
        Task task1 = createTask();
        Task task2 = createTask();
        Task task3 = createTask();
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(List.of(task3), historyManager.getHistory());
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
}