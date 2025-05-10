package com.yandex.taskManager.model;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String toCsv() {
        return String.format("%d,TASK,%s,%s,%s",
                getId(),
                getName(),
                getStatus(),
                getDescription()
        );
    }

    public static Task fromCsv(String csvLine) {
        String[] values = csvLine.split(",", -1);
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim().replaceAll("^\"\"", "");
        }
        TaskType type = TaskType.valueOf(values[1]);

        switch (type) {
            case EPIC:
                Epic epic = new Epic(
                        values[2],
                        values[4],
                        Status.valueOf(values[3])
                );
                epic.setId(Integer.parseInt(values[0]));
                return epic;
            case TASK:
                Task task = new Task(
                        values[2],
                        values[4],
                        Status.valueOf(values[3])
                );
                task.setId(Integer.parseInt(values[0]));
                return task;
            case SUBTASK:
                SubTask subTask = new SubTask(
                        values[2],
                        values[4],
                        Status.valueOf(values[3]),
                        Integer.parseInt(values[5])
                );
                subTask.setId(Integer.parseInt(values[0]));
                return subTask;
        }
        return null;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status);
    }

}
