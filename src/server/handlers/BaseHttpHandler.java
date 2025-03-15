package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import deserializers.DurationDeserializer;
import deserializers.LocalDateTimeDeserializer;
import interfaces.TaskManager;
import validations.TaskIntersectionValidation;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {

    protected TaskManager taskManager;
    protected Gson gson;
    protected TaskIntersectionValidation taskIntersectionValidation;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationDeserializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .setPrettyPrinting()
                .create();
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

    // Отправка успешного ответа без данных (201 Created)
    protected void sendCreated(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, -1);  // Отправка пустого ответа с кодом 201
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
