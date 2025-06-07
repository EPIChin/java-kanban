package com.yandex.taskManager.http;

import com.yandex.taskManager.service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //запуск сервера  на выбор.

        //TaskManager taskManager = new InMemoryTaskManager();
        FileBackedTaskManager taskManager = FileBackedTaskManager.loadFromFile(new File("data"));

        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

        httpTaskServer.start();
    }
}
