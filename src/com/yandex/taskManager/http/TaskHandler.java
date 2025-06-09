package com.yandex.taskManager.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

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
