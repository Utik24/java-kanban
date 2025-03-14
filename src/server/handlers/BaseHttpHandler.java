package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import interfaces.TaskManager;
import validations.TaskIntersectionValidation;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected TaskManager taskManager;
    protected Gson gson;
    protected TaskIntersectionValidation taskIntersectionValidation;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new Gson();
        this.taskIntersectionValidation = new TaskIntersectionValidation();
    }

    public Integer getId(HttpExchange exchange) {
        String[] path = exchange.getRequestURI().getPath().split("/");
        return Integer.parseInt(path[2]);
    }

    protected void sendText(HttpExchange exchange, String response, int code) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    // Отправка ошибки 404 (Not Found)
    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Resource not found";
        exchange.sendResponseHeaders(404, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    // Отправка ошибки 406 (Not Acceptable)
    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String response = "Task intersects with existing tasks";
        exchange.sendResponseHeaders(406, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    // Отправка ошибки 500 (Internal Server Error)
    protected void sendInternalError(HttpExchange exchange) throws IOException {
        String response = "Internal server error";
        exchange.sendResponseHeaders(500, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

}
