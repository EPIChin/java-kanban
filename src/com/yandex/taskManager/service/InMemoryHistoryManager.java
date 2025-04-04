package com.yandex.taskManager.service;


import com.yandex.taskManager.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    protected final List<Task> history =  new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void add(Task task) {
        if (history.size() <=10) {
            history.add(task);
        }
        else {
            history.removeFirst();
            history.add(task);
        }
    }
}