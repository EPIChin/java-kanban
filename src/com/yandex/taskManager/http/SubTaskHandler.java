package com.yandex.taskManager.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

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
                        sendJsonResponse(exchange, subtasks, 200);
                        return;
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("?id=") + 4));
                            Task subtask = taskManager.getSubTaskById(id);
                            if (subtask != null) {
                                sendJsonResponse(exchange, subtask, 200);
                            } else {
                                sendTextResponse(exchange, "Подзадача не найдена", 404);
                            }
                        } catch (StringIndexOutOfBoundsException e) {
                            sendTextResponse(exchange, "В запросе отсутствует необходимый параметр id", 400);
                        } catch (NumberFormatException e) {
                            sendTextResponse(exchange, "Неверный формат id", 400);
                        }
                    }
                }
                case "POST" -> {
                    try (InputStream inputStream = exchange.getRequestBody()) {
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        SubTask subtask = gson.fromJson(body, SubTask.class);
                        if (!Objects.isNull(subtask.getId())) {
                            taskManager.addSubTask(subtask);
                            sendTextResponse(exchange, "Задача создана", 201);
                        } else {
                            taskManager.updateSubTask(subtask);
                            sendTextResponse(exchange, "Задача обновлена", 201);
                        }
                    } catch (JsonSyntaxException e) {
                        sendTextResponse(exchange, "Неверный формат запроса", 400);
                    } catch (IllegalArgumentException e) {
                        sendTextResponse(exchange, "Ошибка: задача имеет пересечение по времени", 406);
                    }
                }
                case "DELETE" -> {
                    if (query == null) {
                        taskManager.deleteAll();
                        sendTextResponse(exchange, "Все подзадачи удалены", 200);
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("?id=") + 4));
                            taskManager.deleteSubTask(id);
                            sendTextResponse(exchange, "Подзадача удалена", 200);
                        } catch (StringIndexOutOfBoundsException e) {
                            sendTextResponse(exchange, "В запросе отсутствует необходимый параметр id", 400);
                        } catch (NumberFormatException e) {
                            sendTextResponse(exchange, "Неверный формат id", 400);
                        }
                    }
                }
                default -> {
                    sendTextResponse(exchange, "Некорректный запрос", 400);
                }
            }
        } catch (Exception e) {
            sendTextResponse(exchange, "Произошла ошибка: " + e.getMessage(), 406);
        }
    }
}
