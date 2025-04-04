package com.yandex.taskManager.model;



import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> idSubTasks = new ArrayList<>();

    public List<Integer> getIdSubTasks() {
        ArrayList<Integer> idSubTasksCopy = new ArrayList<>(idSubTasks);
        return idSubTasksCopy;
    }

    public void addSubTaskId(int idSubTask) {
        idSubTasks.add(idSubTask);
    }

    public void removeSubTaskId(int idSubTask) {
        idSubTasks.remove(idSubTask);
    }

    public Epic(String name, String description) {
        super(name, description);
    }


    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
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