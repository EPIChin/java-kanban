package com.yandex.taskManager.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import com.yandex.taskManager.service.TaskManager;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static final String URL = "http://localhost:";
    private final HttpServer httpServer;
    protected Gson gson;

    private final TaskHandler taskHandler;
    private final SubTaskHandler subtaskHandler;
    private final EpicHandler epicHandler;
    private final HistoryHandler historyHandler;
    private final SubTasksEpicHandler subtasksEpicHandler;
    private final PrioritizedHandler prioritizedHandler;

    public HttpTaskServer(TaskManager managers) throws IOException {
        TaskManager taskManager = managers;
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        taskHandler = new TaskHandler(taskManager, gson);
        subtaskHandler = new SubTaskHandler(taskManager, gson);
        epicHandler = new EpicHandler(taskManager, gson);
        historyHandler = new HistoryHandler(taskManager, gson);
        subtasksEpicHandler = new SubTasksEpicHandler(taskManager, gson);
        prioritizedHandler = new PrioritizedHandler(taskManager, gson);

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/task", taskHandler::handleRequest);
        httpServer.createContext("/subtask", subtaskHandler::handleRequest);
        httpServer.createContext("/epic", epicHandler::handleRequest);
        httpServer.createContext("/history", historyHandler::handleRequest);
        httpServer.createContext("/subtask/epic", subtasksEpicHandler::handleRequest);
        httpServer.createContext("/prioritized", prioritizedHandler::handleRequest);
    }

    public void start() {
        System.out.println("Стартуем сервер на порту " + PORT);
        System.out.println(URL + PORT);
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    public static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(formatter));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }
}

