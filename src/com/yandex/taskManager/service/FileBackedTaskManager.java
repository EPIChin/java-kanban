package com.yandex.taskManager.service;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.model.TaskType;
import com.yandex.taskManager.model.Status;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private File file;

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
    }

    public static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(String message) {
            super(message);
        }

        public ManagerSaveException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic\n");

            for (Task task : getTasks()) {
                fileWriter.write(task.toCsv() + "\n");
            }

            for (Epic epic : getEpics()) {
                fileWriter.write(epic.toCsv() + "\n");
            }

            for (SubTask subTask : getSubTasks()) {
                fileWriter.write(subTask.toCsv() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }

    private TaskType getType(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof SubTask) {
            return TaskType.SUBTASK;
        }
        return TaskType.TASK;
    }

    private String toString(Task task) {
        String[] arrayStringsCSV = {
                Integer.toString(task.getId()),
                getType(task).toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                getParentEpicId(task)};
        return String.join(",", arrayStringsCSV);
    }

    private String getParentEpicId(Task task) {
        if (task instanceof SubTask) {
            return Integer.toString(((SubTask) task).getEpicId());
        }
        return "";
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager tasksManager = new FileBackedTaskManager(file);
        int idMax = 0;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            bufferedReader.readLine();

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isBlank()) break;

                Task task = Task.fromCsv(line);
                if (task != null) {
                    switch (task.getType()) {
                        case EPIC:
                            tasksManager.addEpic((Epic) task);
                            break;
                        case SUBTASK:
                            tasksManager.addSubTask((SubTask) task);
                            break;
                        case TASK:
                            tasksManager.addTask(task);
                            break;
                    }

                    idMax = getIdMax(tasksManager.getTasks(), idMax);
                    idMax = getIdMax(tasksManager.getSubTasks(), idMax);
                    idMax = getIdMax(tasksManager.getEpics(), idMax);
                }
            }
            tasksManager.id = idMax;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла", e);
        }
        return tasksManager;
    }


    private static <T extends Task> int getIdMax(List<T> tasks, int idMax) {
        for (T maxTask : tasks) {
            if (idMax < maxTask.getId()) {
                idMax = maxTask.getId();
            }
        }
        return idMax;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return super.getSubTasks();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void deleteTask() {
        super.deleteTask();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubTask() {
        super.deleteSubTask();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpics(Epic epic) {
        super.updateEpics(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpics(int id) {
        super.deleteEpics(id);
        save();
    }

    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        save();
    }
}
