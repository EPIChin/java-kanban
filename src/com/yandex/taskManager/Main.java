package com.yandex.taskManager;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.ManagerTask;


public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");
        ManagerTask task = new  ManagerTask();
        task.addTask(new Task("починть машину", "важно"));
        task.addTask(new Task("починть телефон", " Очень важно", Status.IN_PROGRESS));

        task.addEpic(new Epic("Уборка!", "пора"));
        task.addEpic(new Epic("Учеба", " важно"));

        task.addSubTask(new SubTask("Уборка в кухне", "помыть посуду", Status.IN_PROGRESS, 2));
        task.addSubTask(new SubTask("Уборка на балконе", "Убрать велосипед", Status.IN_PROGRESS, 2));
        task.addSubTask(new SubTask("Прочитать", "Книгу", Status.DONE, 3));

        task.updateTask(new Task(1, "починить телефон", " Очень важно", Status.DONE));

        System.out.println(task.getTasks());
        System.out.println(task.getEpics());
        System.out.println(task.getSubTasks());

        System.out.println(task.getAllSubtasksOfEpic(2));

    }
}
