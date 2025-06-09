package com.yandex.taskManager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.taskManager.service.TaskManager;

import java.io.IOException;

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
