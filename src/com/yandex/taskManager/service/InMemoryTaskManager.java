package com.yandex.taskManager.service;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, SubTask> subTasks = new HashMap<>();

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
    );

    @Override
    public void addTask(Task task) {
        if (hasOverlappingTasks(task)) {
            throw new IllegalArgumentException("Задача пересекается с существующими задачами");
        }
        id++;
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        id++;
        epic.setId(id);
        epics.put(id, epic);
        updateTimeEpic(epic);
        prioritizedTasks.add(epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getEpicId());
        id++;
        subTask.setId(id);
        epic.setIdSubTasks(id);
        subTasks.put(id, subTask);
        prioritizedTasks.add(subTask);

        updateEpicFields(epic, subTask);
    }

    private void updateEpicFields(Epic epic, SubTask subTask) {
        epic.setStatus(checkStatusEpic(subTask));
        updateTimeEpic(epic);
        Task oldTask = getEpicById(epic.getId());
        prioritizedTasks.remove(oldTask);
        epics.put(epic.getId(), epic);
        prioritizedTasks.add(epic);
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
    public SubTask getSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        }
        return null;
    }

    @Override
    public void updateTask(Task task) {
        Task oldTask = getTaskById(task.getId());
        prioritizedTasks.remove(oldTask);
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else tasks.put(id++, task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateEpics(Epic epic) {
        Task oldTask = getEpicById(epic.getId());
        prioritizedTasks.remove(oldTask);
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else epics.put(id++, epic);
        prioritizedTasks.add(epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        prioritizedTasks.remove(subTask);
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            updateTimeEpic(epics.get(subTask.getEpicId()));
        } else subTasks.put(id++, subTask);
        prioritizedTasks.add(subTask);
        updateEpicFields(epics.get(subTask.getEpicId()), subTask);
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
        prioritizedTasks.remove(tasks.get(id));
    }

    @Override
    public void deleteEpics(int id) {
        epics.remove(id);
        prioritizedTasks.remove(epics.get(id));
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        subTasks.remove(subTask.getId());
        updateTimeEpic(epics.get(subTasks.get(subTask.getId()).getEpicId()));
        prioritizedTasks.remove(subTasks.get(subTask.getId()));
    }

    @Override
    public ArrayList<SubTask> getAllSubtasksOfEpic(int id) {
        Epic epic = epics.get(id);
        return epic.getIdSubTasks().stream()
                .map(this::getSubTaskById)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Status checkStatusEpic(SubTask subTask) {
        int epicId = subTask.getEpicId();

        if (epics.get(epicId).getIdSubTasks().isEmpty() || compareStatus(epics.get(epicId).getIdSubTasks(), Status.NEW)) {
            return Status.NEW;
        } else if (compareStatus(epics.get(epicId).getIdSubTasks(), Status.DONE)) {
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
    public void deleteAll() {
        subTasks.clear();
        epics.clear();
        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateTimeEpic(Epic epic) {
        ArrayList<SubTask> idSubTasks = getAllSubtasksOfEpic(epic.getId());
        epic.setStartTime(calculateStartTimeEpic(idSubTasks));
        epic.setEndTime(calculateEndTimeEpic(idSubTasks));
        epic.setDuration(calculateDurationEpic(idSubTasks));
    }

    private long calculateDurationEpic(ArrayList<SubTask> subTasks) {
        return subTasks.stream()
                .mapToLong(Task::getDuration)
                .sum();
    }

    private LocalDateTime calculateEndTimeEpic(ArrayList<SubTask> subTasks) {
        if (!subTasks.isEmpty()) {
            LocalDateTime endTime = subTasks.getFirst().getEndTime();
            for (SubTask subTask : subTasks) {
                if (subTask.getEndTime().isAfter(endTime)) {
                    endTime = subTask.getEndTime();
                }
            }
            return endTime;
        }
        return LocalDateTime.now();
    }

    private LocalDateTime calculateStartTimeEpic(ArrayList<SubTask> subTasks) {
        if (!subTasks.isEmpty()) {
            LocalDateTime startTime = subTasks.getFirst().getStartTime();
            for (SubTask subTask : subTasks) {
                if (subTask.getStartTime().isBefore(startTime)) {
                    startTime = subTask.getStartTime();
                }
            }
            return startTime;
        } else {
            return LocalDateTime.now();
        }
    }

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private boolean isOverlapping(Task task1, Task task2) {
        return task1.getStartTime().isBefore(task2.getEndTime()) &&
                task2.getStartTime().isBefore(task1.getEndTime());
    }

    private boolean hasOverlappingTasks(Task task) {
        return prioritizedTasks.stream()
                .filter(t -> !t.equals(task))
                .anyMatch(t -> isOverlapping(task, t));
    }
}

