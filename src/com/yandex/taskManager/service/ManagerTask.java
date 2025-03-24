package com.yandex.taskManager.service;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SequencedCollection;

public class  ManagerTask {
    private int id = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public  void addTask(Task task) {
        tasks.put(id++, task);
    }

    public void addEpic(Epic epic) {
        epics.put(id++, epic);
    }

    public void addSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        epic.setIdSubTasks(id);
        epics.put(subTask.getEpicId(), epic);
        subTasks.put(id++, subTask);

        epic.setStatus(checkStatusEpic(subTask));
        epic.setStatus(checkStatusEpic(subTask));
        epic.setId(subTask.getEpicId());
        updateEpics(epic);
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void deleteTask() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subTasks.clear();
    }

    public void deleteSubTask() {
        subTasks.clear();
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        }
        return null;
    }

    public Task getEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        }
        return null;
    }

    public Task getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            return subTasks.get(id);
        }
        return null;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else tasks.put(id++, task);
    }

    public void updateEpics(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else epics.put(id++, epic);
    }

    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
        } else subTasks.put(id++, subTask);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpics(int id) {
        epics.remove(id);
    }

    public void deleteSubTask(int id) {
        subTasks.remove(id);
    }

    public SequencedCollection<Task> getAllSubtasksOfEpic(int id) {
        epics.get(id);
        Epic epic = epics.get(id);
        List<Task> idSubTasks = new ArrayList<>();
        for (int i : epic.getIdSubTasks()) {
            idSubTasks.add(getSubTaskById(i));
        }
        return idSubTasks;
    }
    public Status checkStatusEpic(SubTask subTask) {
        int EpicId = subTask.getEpicId();

        if (epics.get(EpicId).getIdSubTasks().isEmpty() || compareStatus(epics.get(EpicId).getIdSubTasks(), Status.NEW)) {
            return Status.NEW;
        } else if (compareStatus(epics.get(EpicId).getIdSubTasks(), Status.DONE)) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }

    private boolean compareStatus(List<Integer> tasks, Status status) {
        for (Integer task : tasks) {
            if (!subTasks.get(task).getStatus().equals(status)) {
                return false;
            }
        }
        return true;
    }
}
