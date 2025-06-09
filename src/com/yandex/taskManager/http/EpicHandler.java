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
                        sendJsonResponse(exchange, epics, 200);
                        return;
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("?id=") + 4));
                            Task epic = taskManager.getEpicById(id);
                            if (epic != null) {
                                sendJsonResponse(exchange, epic, 200);
                            } else {
                                sendTextResponse(exchange, "Эпик не найден", 404);
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
                        Epic epic = gson.fromJson(body, Epic.class);
                        if (!Objects.isNull(epic.getId())) {
                            taskManager.addEpic(epic);
                            sendTextResponse(exchange, "Эпик создан", 201);
                        } else {
                            taskManager.updateEpics(epic);
                            sendTextResponse(exchange, "Эпик обновлен", 201);
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
                        sendTextResponse(exchange, "Все эпики удалены", 200);
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(query.indexOf("?id=") + 4));
                            taskManager.deleteEpics(id);
                            sendTextResponse(exchange, "Эпик удален", 200);
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
