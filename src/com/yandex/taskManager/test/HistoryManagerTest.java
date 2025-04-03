package com.yandex.taskManager.test;

import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.HistoryManager;
import com.yandex.taskManager.service.InMemoryHistoryManager;
import org.junit.jupiter.api.Test;

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

}