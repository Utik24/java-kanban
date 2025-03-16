package server.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.IntersectionException;
import interfaces.TaskManager;
import model.Task;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("POST".equals(method)) {
            String json = new String(exchange.getRequestBody().readAllBytes());
            Task task = gson.fromJson(json, Task.class);
            try {
                createTaskOrUpdateTask(task, exchange);
            } catch (JsonSyntaxException e) {
                System.err.println("JSON Syntax Error: " + e.getMessage());
                sendInternalError(exchange);
            }
        } else if ("GET".equals(method)) {
            showAllOrIdTasks(exchange);
        } else if ("DELETE".equals(method)) {
            if (getId(exchange) != null) {
                deleteTask(exchange);
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);  // Метод не поддерживается
        }
    }

    public void createTaskOrUpdateTask(Task task, HttpExchange exchange) throws IOException {
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length == 3) {
            try {
                int id = getId(exchange);
                task.setId(id);
                taskManager.updateTask(task);
            } catch (IntersectionException e) {
                sendHasInteractions(exchange);
            }
        } else {
            taskManager.createTask(task);
        }
        sendCreated(exchange);
    }

    public void showAllOrIdTasks(HttpExchange exchange) throws IOException {
        String response = "";
        String[] path = exchange.getRequestURI().getPath().split("/");
        if (path.length != 3) {
            response = gson.toJson(taskManager.getAllTasks());
        } else {
            int id = getId(exchange);
            if (taskManager.getTaskById(id) != null) {
                response = gson.toJson(taskManager.getTaskById(id));
            } else {
                sendNotFound(exchange);
            }
        }
        sendText(exchange, response, 200);
    }

    public void deleteTask(HttpExchange exchange) throws IOException {
        int id = getId(exchange);
        taskManager.removeTaskById(id);
        sendDeleted(exchange);

    }
}
