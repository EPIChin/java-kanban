package com.yandex.taskManager.service;

import com.yandex.taskManager.model.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void add(Task task);
    }