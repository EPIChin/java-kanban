package com.yandex.taskManager.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.taskManager.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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
