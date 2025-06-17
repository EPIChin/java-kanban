package com.yandex.taskManager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.taskManager.model.Task;
import com.yandex.taskManager.service.TaskManager;

import java.io.IOException;
import java.util.List;

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
                sendJsonResponse(exchange, history, 200);
            } else {
                sendTextResponse(exchange, "Некорректный запрос", 400);
            }
        } catch (Exception e) {
            sendTextResponse(exchange, "Произошла ошибка: " + e.getMessage(), 406);
        }
    }
}
