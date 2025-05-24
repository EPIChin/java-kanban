package com.yandex.taskManager.model;

import java.time.LocalDateTime;

public class SubTask extends Task {

    private Integer epicId;


    public SubTask(int id, String name, String description, Status status, Integer epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Status status, LocalDateTime startTime, long duration, Integer epicId) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toCsv() {
        return String.format("%d,SUBTASK,%s,%s,%s,%s,%s,%d",
                getId(),
                getName(),
                getStatus(),
                getDescription(),
                getStartTime(),
                getDuration(),
                getEpicId()
        );
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicId=" + epicId +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + startTime +
                ", endTime=" + getEndTime() +
                ", duration=" + duration +
                '}';
    }
}