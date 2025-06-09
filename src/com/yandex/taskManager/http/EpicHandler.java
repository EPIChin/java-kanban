package com.yandex.taskManager.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.taskManager.model.Epic;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

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
