package com.yandex.taskManager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.taskManager.model.SubTask;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.TaskManager;

import java.io.IOException;
import java.util.ArrayList;

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
                        sendJsonResponse(exchange, subtasks, 200);
                    } else {
                        sendTextResponse(exchange, "Эпик не найден", 404);
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    sendTextResponse(exchange, "В запросе отсутствует id", 400);
                } catch (NumberFormatException e) {
                    sendTextResponse(exchange, "Неверный формат id", 400);
                }
            }
        } catch (Exception e) {
            sendTextResponse(exchange, "Произошла ошибка: " + e.getMessage(), 406);
        }
    }
}
