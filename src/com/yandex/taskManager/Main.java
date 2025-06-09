package com.yandex.taskManager;

import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Status;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.FileBackedTaskManager;
import com.yandex.taskManager.service.InMemoryTaskManager;

import java.io.File;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        System.out.println("Поехали!");
        InMemoryTaskManager manager = new InMemoryTaskManager();
        manager.addTask(new Task("починить машину", "важно", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(300), 5));
        manager.addTask(new Task("починить телефон", " Очень важно", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(30), 5));
        //manager.addTask(new Task("починить смартфон", " Очень важно", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(30),5)); // пересечение по времени

        manager.addEpic(new Epic("Уборка!", "пора", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(10), 5));
        manager.addEpic(new Epic("Учеба", " важно", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(70), 5));

        manager.addSubTask(new SubTask("Уборка в кухне", "помыть посуду", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(20), 5, 3));
        manager.addSubTask(new SubTask("Уборка на балконе", "Убрать велосипед", Status.IN_PROGRESS, LocalDateTime.now().plusMinutes(100), 5, 3));

        manager.updateTask(new Task(1, "починить телефон", " Очень важно", Status.DONE, LocalDateTime.now().plusMinutes(30), 5));

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());
        System.out.println();

        System.out.println("Задача " + manager.getTaskById(1));
        System.out.println();
        System.out.println("Все задачи Эпики'2'" + manager.getAllSubtasksOfEpic(3));
        System.out.println();
        System.out.println("История:" + manager.getHistory());
        System.out.println();

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new File("data"));

        fileBackedTaskManager.addTask(new Task("починить телефон", " Очень важно", Status.IN_PROGRESS, LocalDateTime.now(), 5));
        fileBackedTaskManager.addEpic(new Epic("Учеба", " важно", Status.IN_PROGRESS, LocalDateTime.now(), 5));

        FileBackedTaskManager loadFileBackedTaskManager = FileBackedTaskManager.loadFromFile(new File("data"));
        System.out.println("прочитано" + loadFileBackedTaskManager.getTasks());
        System.out.println();
        System.out.println("список задач в порядке приоритета" + manager.getPrioritizedTasks());

    }
}
