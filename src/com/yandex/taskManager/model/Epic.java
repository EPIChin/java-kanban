package com.yandex.taskManager.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> idSubTasks = new ArrayList<>();

    public List<Integer> getIdSubTasks() {
        ArrayList<Integer> idSubTasksCopy = new ArrayList<>(idSubTasks);
        return idSubTasksCopy;
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

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + idSubTasks +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}