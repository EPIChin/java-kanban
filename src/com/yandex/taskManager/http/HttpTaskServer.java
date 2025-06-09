package com.yandex.taskManager.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.TaskManager;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

abstract class BaseHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    protected abstract void handleRequest(HttpExchange exchange) throws IOException;
}

class TaskHandler extends BaseHandler {
    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode = 200;
        String requestMethod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        try {
            switch (requestMethod) {
                case "GET" -> {
                    if (query == null) {
                        List<Task> tasks = taskManager.getTasks();
                        response = gson.toJson(tasks);
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("?id=") + 4));
                            Task task = taskManager.getTaskById(id);
                            if (task != null) {
                                response = gson.toJson(task);
                            } else {
                                response = "Задача не найдена";
                                statusCode = 404;
                            }
                        } catch (StringIndexOutOfBoundsException e) {
                            response = "В запросе отсутствует необходимый параметр id";
                            statusCode = 400;
                        } catch (NumberFormatException e) {
                            response = "Неверный формат id";
                            statusCode = 400;
                        }
                    }
                }
                case "POST" -> {
                    try (InputStream inputStream = exchange.getRequestBody()) {
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Task task = gson.fromJson(body, Task.class);
                        if (!Objects.isNull(task.getId())) {
                            taskManager.addTask(task);
                            statusCode = 201;
                            response = "Задача создана";
                        } else {
                            taskManager.updateTask(task);
                            statusCode = 201;
                            response = "Задача обновлена";
                        }
                    } catch (JsonSyntaxException e) {
                        response = "Неверный формат запроса";
                        statusCode = 400;
                    } catch (IllegalArgumentException e) {
                        response = "Ошибка: задача имеет пересечение по времени";
                        statusCode = 406;
                    }
                }
                case "DELETE" -> {
                    if (query == null) {
                        taskManager.deleteAll();
                        response = "Все задачи удалены";
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("?id=") + 4));
                            taskManager.deleteTask(id);
                            response = "Задача удалена";
                        } catch (StringIndexOutOfBoundsException e) {
                            response = "В запросе отсутствует необходимый параметр id";
                            statusCode = 400;
                        } catch (NumberFormatException e) {
                            response = "Неверный формат id";
                            statusCode = 400;
                        }
                    }
                }
                default -> {
                    response = "Некорректный запрос";
                    statusCode = 400;
                }
            }
        } catch (Exception e) {
            response = "Произошла ошибка: " + e.getMessage();
            statusCode = 406;
        }

        sendResponse(exchange, response, statusCode);
    }
}

class SubTaskHandler extends BaseHandler {
    public SubTaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode = 200;
        String requestMethod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        try {
            switch (requestMethod) {
                case "GET" -> {
                    if (query == null) {
                        ArrayList<SubTask> subtasks = taskManager.getSubTasks();
                        response = gson.toJson(subtasks);
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("?id=") + 4));
                            Task subtask = taskManager.getSubTaskById(id);
                            if (subtask != null) {
                                response = gson.toJson(subtask);
                            } else {
                                response = "Подзадача не найдена";
                                statusCode = 404;
                            }
                        } catch (StringIndexOutOfBoundsException e) {
                            response = "В запросе отсутствует необходимый параметр id";
                            statusCode = 400;
                        } catch (NumberFormatException e) {
                            response = "Неверный формат id";
                            statusCode = 400;
                        }
                    }
                }
                case "POST" -> {
                    try (InputStream inputStream = exchange.getRequestBody()) {
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        SubTask subtask = gson.fromJson(body, SubTask.class);
                        if (!Objects.isNull(subtask.getId())) {
                            taskManager.addSubTask(subtask);
                            statusCode = 201;
                            response = "Задача создана";
                        } else {
                            taskManager.updateSubTask(subtask);
                            statusCode = 201;
                            response = "Задача обновлена";
                        }
                    } catch (JsonSyntaxException e) {
                        response = "Неверный формат запроса";
                        statusCode = 400;
                    } catch (IllegalArgumentException e) {
                        response = "Ошибка: задача имеет пересечение по времени";
                        statusCode = 406;
                    }
                }
                case "DELETE" -> {
                    if (query == null) {
                        taskManager.deleteAll();
                        response = "Все подзадачи удалены";
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("?id=") + 4));
                            taskManager.deleteSubTask(id);
                            response = "Подзадача удалена";
                        } catch (StringIndexOutOfBoundsException e) {
                            response = "В запросе отсутствует необходимый параметр id";
                            statusCode = 400;
                        } catch (NumberFormatException e) {
                            response = "Неверный формат id";
                            statusCode = 400;
                        }
                    }
                }
                default -> {
                    response = "Некорректный запрос";
                    statusCode = 400;
                }
            }
        } catch (Exception e) {
            response = "Произошла ошибка: " + e.getMessage();
            statusCode = 406;
        }

        sendResponse(exchange, response, statusCode);
    }
}

class EpicHandler extends BaseHandler {
    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode = 200;
        String requestMethod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        try {
            switch (requestMethod) {
                case "GET" -> {
                    if (query == null) {
                        ArrayList<Epic> epics = taskManager.getEpics();
                        response = gson.toJson(epics);
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("?id=") + 4));
                            Task epic = taskManager.getEpicById(id);
                            if (epic != null) {
                                response = gson.toJson(epic);
                            } else {
                                response = "Эпик не найден";
                                statusCode = 404;
                            }
                        } catch (StringIndexOutOfBoundsException e) {
                            response = "В запросе отсутствует необходимый параметр id";
                            statusCode = 400;
                        } catch (NumberFormatException e) {
                            response = "Неверный формат id";
                            statusCode = 400;
                        }
                    }
                }
                case "POST" -> {
                    try (InputStream inputStream = exchange.getRequestBody()) {
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic = gson.fromJson(body, Epic.class);
                        if (!Objects.isNull(epic.getId())) {
                            taskManager.addEpic(epic);
                            statusCode = 201;
                            response = "Эпик создан";
                        } else {
                            taskManager.updateEpics(epic);
                            statusCode = 201;
                            response = "Эпик обновлен";
                        }
                    } catch (JsonSyntaxException e) {
                        response = "Неверный формат запроса";
                        statusCode = 400;
                    } catch (IllegalArgumentException e) {
                        response = "Ошибка: задача имеет пересечение по времени";
                        statusCode = 406;
                    }
                }
                case "DELETE" -> {
                    if (query == null) {
                        taskManager.deleteAll();
                        response = "Все эпики удалены";
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("?id=") + 4));
                            taskManager.deleteEpics(id);
                            response = "Эпик удален";
                        } catch (StringIndexOutOfBoundsException e) {
                            response = "В запросе отсутствует необходимый параметр id";
                            statusCode = 400;
                        } catch (NumberFormatException e) {
                            response = "Неверный формат id";
                            statusCode = 400;
                        }
                    }
                }
                default -> {
                    response = "Некорректный запрос";
                    statusCode = 400;
                }
            }
        } catch (Exception e) {
            response = "Произошла ошибка: " + e.getMessage();
            statusCode = 406;
        }

        sendResponse(exchange, response, statusCode);
    }
}

class HistoryHandler extends BaseHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode = 200;
        String requestMethod = exchange.getRequestMethod();

        try {
            if ("GET".equals(requestMethod)) {
                List<Task> history = taskManager.getHistory();
                response = gson.toJson(history);
            } else {
                response = "Некорректный запрос";
                statusCode = 400;
            }
        } catch (Exception e) {
            response = "Произошла ошибка: " + e.getMessage();
            statusCode = 406;
        }

        sendResponse(exchange, response, statusCode);
    }
}

class SubTasksEpicHandler extends BaseHandler {
    public SubTasksEpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode = 200;
        String requestMethod = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();

        try {
            if ("GET".equals(requestMethod)) {
                try {
                    int id = Integer.parseInt(query.substring(query.indexOf("?id=") + 4));
                    Task epic = taskManager.getEpicById(id);
                    if (epic != null) {
                        ArrayList<SubTask> subtasks = taskManager.getAllSubtasksOfEpic(epic.getId());
                        response = gson.toJson(subtasks);
                    } else {
                        response = "Эпик не найден";
                        statusCode = 404;
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    response = "В запросе отсутствует id";
                    statusCode = 400;
                } catch (NumberFormatException e) {
                    response = "Неверный формат id";
                    statusCode = 400;
                }
            }
        } catch (Exception e) {
            response = "Произошла ошибка: " + e.getMessage();
            statusCode = 406;
        }

        sendResponse(exchange, response, statusCode);
    }
}

class PrioritizedHandler extends BaseHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode = 200;
        String requestMethod = exchange.getRequestMethod();

        if ("GET".equals(requestMethod)) {
            response = gson.toJson(taskManager.getPrioritizedTasks());
        } else {
            response = "Некорректный запрос";
            statusCode = 400;
        }

        sendResponse(exchange, response, statusCode);
    }
}

