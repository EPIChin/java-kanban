package com.yandex.taskManager.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> idSubTasks = new ArrayList<>();
    private LocalDateTime endTime;

    public List<Integer> getIdSubTasks() {
        return new ArrayList<>(idSubTasks);
    }

    public void setIdSubTasks(int idSubTask) {
        idSubTasks.add(idSubTask);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description, Status status, LocalDateTime startTime, long duration) {
        super(name, description, status, startTime, duration);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toCsv() {
        return String.format("%d,EPIC,%s,%s,%s,%s,%s",
                getId(),
                getName(),
                getStatus(),
                getDescription(),
                getStartTime(),
                getDuration()
        );
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + idSubTasks +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                ", duration=" + duration +
                '}';
    }
}