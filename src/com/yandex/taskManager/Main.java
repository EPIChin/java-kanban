package com.yandex.taskManager;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.FileBackedTaskManager;
import com.yandex.taskManager.service.InMemoryTaskManager;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.addTask(new Task("починть машину", "важно"));
        manager.addTask(new Task("починть телефон", " Очень важно", Status.IN_PROGRESS));

        manager.addEpic(new Epic("Уборка!", "пора"));
        manager.addEpic(new Epic("Учеба", " важно"));

        manager.addSubTask(new SubTask("Уборка в кухне", "помыть посуду", Status.IN_PROGRESS, 3));
        manager.addSubTask(new SubTask("Уборка на балконе", "Убрать велосипед", Status.IN_PROGRESS, 3));
        manager.addSubTask(new SubTask("Прочитать", "Книгу", Status.DONE, 4));

        manager.updateTask(new Task(1, "починить телефон", " Очень важно", Status.DONE));

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        System.out.println("Задача " + manager.getTaskById(1));
        System.out.println("Все задачи Эпики'2'" + manager.getAllSubtasksOfEpic(3));
        System.out.println("История:" + manager.getHistory());


        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new File("data"));

        fileBackedTaskManager.addTask(new Task("починить машину", "важно"));
        fileBackedTaskManager.addEpic(new Epic("Уборка!", "пора"));
        fileBackedTaskManager.addEpic(new Epic("Учеба", " важно"));

        FileBackedTaskManager loadFileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("data"));
        System.out.println("прочитано" + loadFileBackedTaskManager.getTasks());


    }
}
