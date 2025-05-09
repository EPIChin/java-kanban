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
                fileWriter.write(toString(task) + "\n");
            }

            for (Epic epic : getEpics()) {
                fileWriter.write(toString(epic) + "\n");
            }

            for (SubTask subTask : getSubTasks()) {
                fileWriter.write(toString(subTask) + "\n");
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
                if (line.isBlank()) {
                    break;
                }

                Task task = fromString(line);

                if (task instanceof Epic) {
                    tasksManager.addEpic((Epic) task);
                } else if (task instanceof SubTask) {
                    tasksManager.addSubTask((SubTask) task);
                } else if (task != null) {
                    tasksManager.addTask(task);
                } else {
                    System.out.println("Неверная задача");
                }
                if (task != null) {
                    idMax = getIdMax(tasksManager.getTasks(), idMax);
                    idMax = getIdMax(tasksManager.getSubTasks(), idMax);
                    idMax = getIdMax(tasksManager.getEpics(), idMax);
                    idMax++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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

    private static Task fromString(String value) {
        String[] values = value.split(",");
        String id = values[0];
        String type = values[1];
        String name = values[2];
        String status = values[3];
        String description = values[4];
        Integer idOfEpic = type.equals(TaskType.SUBTASK.toString()) ? Integer.valueOf(values[5]) : null;
        switch (type) {
            case "EPIC":
                Epic epic = new Epic(name, description, Status.valueOf(status.toUpperCase()));
                epic.setId(Integer.parseInt(id));
                epic.setStatus(Status.valueOf(status.toUpperCase()));
                return epic;
            case "SUBTASK":
                SubTask subTask = new SubTask(name, description, Status.valueOf(status.toUpperCase()), idOfEpic);
                subTask.setId(Integer.parseInt(id));
                return subTask;
            case "TASK":
                Task task = new Task(name, description, Status.valueOf(status.toUpperCase()));
                task.setId(Integer.parseInt(id));
                return task;
            default:
                return null;
        }
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
