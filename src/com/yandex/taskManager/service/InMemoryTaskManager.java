package com.yandex.taskManager.service;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public  void addTask(Task task) {
        tasks.put(id++, task);
    }

    @Override
    public void addEpic(Epic epic) {
        epics.put(id++, epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        epic.addSubTaskId(id);
        epics.put(subTask.getEpicId(), epic);
        subTasks.put(id++, subTask);

        epic.setStatus(checkStatusEpic(subTask));
        epic.setId(subTask.getEpicId());
        updateEpics(epic);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteTask() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteSubTask() {
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        }
        return null;
    }

    @Override
    public Task getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }
        return null;
    }

    @Override
    public Task getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        }
        return null;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else tasks.put(id++, task);
    }

    @Override
    public void updateEpics(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else epics.put(id++, epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
        } else subTasks.put(id++, subTask);
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpics(int id) {
        epics.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        Epic epic = epics.get(subTasks.get(id).getEpicId());
        epic.removeSubTaskId(id);

        epic.setStatus(checkStatusEpic(subTasks.get(id)));
        updateEpics(epic);
        subTasks.remove(id);
    }

    @Override
    public ArrayList<Task> getAllSubtasksOfEpic(int id) {
        epics.get(id);
        Epic epic = epics.get(id);
        ArrayList<Task> idSubTasks = new ArrayList<>();
        for (int i : epic.getIdSubTasks()) {
            idSubTasks.add(getSubTaskById(i));
        }
        return idSubTasks;
    }

    @Override
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

    @Override
    public List<Task> getHistory() {
        return  historyManager.getHistory();
    }
}
